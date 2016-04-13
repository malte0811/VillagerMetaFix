package malte0811.villagerMeta;

import java.util.Iterator;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;

public class VillageTransformer implements IClassTransformer, Opcodes {
	private static boolean patchedList = false, patchedRecipe = false, obf = false;
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!patchedRecipe&&name.equals("net.minecraft.village.MerchantRecipe")) {
			patchedRecipe = true;
			obf = true;
			return patchRecipe(true, basicClass);
		} else if (!patchedRecipe&&name.equals("agn")) {
			patchedRecipe = true;
			obf = false;
			return patchRecipe(false, basicClass);
		} else if (!patchedList&&name.equals("net.minecraft.village.MerchantRecipeList")) {
			patchedList = true;
			obf = true;
			return patchList(true, basicClass);
		} else if (!patchedList&&name.equals("ago")) {
			patchedList = true;
			obf = false;
			return patchList(false, basicClass);
		} else if (name.equals("malte0811.villagerMeta.api.VillagerHelper")) {
			return patchApi(obf, basicClass);
		}

		return basicClass;
	}

	private byte[] patchRecipe(boolean dev, byte[] base) {
		FMLLog.log(VillageContainer.MODID, Level.INFO, "Patching MerchantRecipe, obfuscated: "+!dev);
		String nbt = dev?"net/minecraft/nbt/NBTTagCompound":"dh";
		String name = dev?"net/minecraft/village/MerchantRecipe":"agn";
		String read = dev?"readFromTags":"a";
		String write = dev?"writeToTags":"i";
		String getBoolean = dev?"getBoolean":"n";
		String setBoolean = dev?"setBoolean":"a";
		String hasKey = dev?"hasKey":"c";
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(base);
		classReader.accept(classNode, 0);

		//add boolean fields
		ClassWriter cw;
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(cw);
		FieldVisitor fv = cw.visitField(ACC_PUBLIC, "checkMeta", "Z", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_PUBLIC, "checkNbt", "Z", null, null);
		fv.visitEnd();
		base = cw.toByteArray();
		classNode = new ClassNode();
		classReader = new ClassReader(base);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals("<init>"))
			{
				InsnList list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new InsnNode(Config.useMetaOnDefault()?ICONST_1:ICONST_0));
				list.add(new FieldInsnNode(PUTFIELD, name, "checkMeta", "Z"));
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new InsnNode(Config.useNbtOnDefault()?ICONST_1:ICONST_0));
				list.add(new FieldInsnNode(PUTFIELD, name, "checkNbt", "Z"));
				m.instructions.insert(list);
			} else if (m.name.equals(read)&&m.desc.equals("(L"+nbt+";)V")) {
				InsnList list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 1));
				list.add(new LdcInsnNode("checkMeta"));
				list.add(new MethodInsnNode(INVOKEVIRTUAL, nbt, hasKey, "(Ljava/lang/String;)Z", false));
				LabelNode label = new LabelNode();
				list.add(new JumpInsnNode(IFEQ, label));
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new VarInsnNode(ALOAD, 1));
				list.add(new LdcInsnNode("checkMeta"));
				list.add(new MethodInsnNode(INVOKEVIRTUAL, nbt, getBoolean, "(Ljava/lang/String;)Z", false));
				list.add(new FieldInsnNode(PUTFIELD, name, "checkMeta", "Z"));
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new VarInsnNode(ALOAD, 1));
				list.add(new LdcInsnNode("checkNbt"));
				list.add(new MethodInsnNode(INVOKEVIRTUAL, nbt, getBoolean, "(Ljava/lang/String;)Z", false));
				list.add(new FieldInsnNode(PUTFIELD, name, "checkNbt", "Z"));
				list.add(label);
				m.instructions.insert(list);
			} else if (m.name.equals(write)&&m.desc.equals("()L"+nbt+";")) {
				InsnList list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 1));
				list.add(new LdcInsnNode("checkNbt"));
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD, name, "checkNbt", "Z"));
				list.add(new MethodInsnNode(INVOKEVIRTUAL, nbt, setBoolean, "(Ljava/lang/String;Z)V", false));

				list.add(new VarInsnNode(ALOAD, 1));
				list.add(new LdcInsnNode("checkMeta"));
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD, name, "checkMeta", "Z"));
				list.add(new MethodInsnNode(INVOKEVIRTUAL, nbt, setBoolean, "(Ljava/lang/String;Z)V", false));

				InsnList method = m.instructions;
				AbstractInsnNode ins = method.getFirst();
				while (!(ins instanceof VarInsnNode)||ins.getOpcode()!=ASTORE) {
					ins = ins.getNext();
				}
				method.insert(ins, list);
			}
		}

		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(cw);
		return cw.toByteArray();
	}

	private byte[] patchList(boolean dev, byte[] base) {
		FMLLog.log(VillageContainer.MODID, Level.INFO, "Patching MerchantRecipeList, obfuscated: "+!dev);
		String stack = dev?"net/minecraft/item/ItemStack":"add";
		String list = dev?"net/minecraft/village/MerchantRecipeList":"ago";
		String recipe = dev?"net/minecraft/village/MerchantRecipe":"agn";
		String canUse = dev?"canRecipeBeUsed":"a";
		String addWithCheck = dev?"addToListWithCheck":"a";
		String get = "get";
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(base);
		classReader.accept(classNode, 0);

		ClassWriter cw;

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(canUse)&&m.desc.equals("(L"+stack+";L"+stack+";I)L"+recipe+";")) {
				InsnList method = m.instructions;
				Iterator<AbstractInsnNode> it = method.iterator();
				while (it.hasNext()) {
					AbstractInsnNode in = it.next();
					if (in instanceof MethodInsnNode) {
						MethodInsnNode node = (MethodInsnNode) in;
						if (node.name.equals(get)&&node.owner.equals(list)) {
							while (!(in instanceof VarInsnNode)||in.getOpcode()!=ASTORE) {
								in = in.getNext();
							}
							InsnList l = new InsnList();
							l.add(new VarInsnNode(ALOAD, ((VarInsnNode)in).var));
							l.add(new VarInsnNode(ALOAD, 1));
							l.add(new VarInsnNode(ALOAD, 2));
							l.add(new MethodInsnNode(INVOKESTATIC, "malte0811/villagerMeta/api/VillagerHelper", "isMetaOrNbtInvalid", "(L"+recipe+";L"+stack+";L"+stack+";)Z", false));
							LabelNode label = new LabelNode();
							l.add(new JumpInsnNode(IFEQ, label));
							l.add(new InsnNode(ACONST_NULL));
							l.add(new InsnNode(ARETURN));
							l.add(label);
							method.insert(in, l);
						}
					}
				}
			} else if (m.name.equals(addWithCheck)&&m.desc.equals("(L"+recipe+";)V")) {
				InsnList method = m.instructions;
				Iterator<AbstractInsnNode> it = method.iterator();
				while (it.hasNext()) {
					AbstractInsnNode in = it.next();
					if (in instanceof JumpInsnNode) {
						JumpInsnNode n = (JumpInsnNode) in;
						if (n.getOpcode()==IFEQ) {
							InsnList l = new InsnList();
							l.add(new VarInsnNode(ALOAD, 1));
							l.add(new VarInsnNode(ALOAD, 3));
							l.add(new MethodInsnNode(INVOKESTATIC, "malte0811/villagerMeta/api/VillagerHelper", "hasSameInputMetaNBT", "(L"+recipe+";L"+recipe+";)Z", false));
							l.add(new JumpInsnNode(IFEQ, n.label));
							method.insert(in, l);
							break;
						}
					}
				}
			}
		}

		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(cw);
		return cw.toByteArray();
	}
	//reflection does not have a good performance
	private byte[] patchApi(boolean dev, byte[] base) {
		FMLLog.log(VillageContainer.MODID, Level.INFO, "Patching VillagerHelper, obfuscated: "+!dev);
		String recipe = dev?"net/minecraft/village/MerchantRecipe":"agn";
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(base);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.desc.equals("(L"+recipe+";)Z")) {
				if (m.name.equals("isMetaSensitive")) {
					m.visitCode();
					m.visitVarInsn(ALOAD, 0);
					m.visitFieldInsn(GETFIELD, recipe, "checkMeta", "Z");
					m.visitInsn(IRETURN);
					m.visitMaxs(1, 1);
					m.visitEnd();
				} else if (m.name.equals("isNbtSensitive")) {
					m.visitCode();
					m.visitVarInsn(ALOAD, 0);
					m.visitFieldInsn(GETFIELD, recipe, "checkNbt", "Z");
					m.visitInsn(IRETURN);
					m.visitMaxs(1, 1);
					m.visitEnd();
				} else if (m.name.equals("setMetaAndNbtSensitivity")) {
					m.visitCode();
					m.visitVarInsn(ALOAD, 0);
					m.visitVarInsn(ILOAD, 1);
					m.visitFieldInsn(PUTFIELD, recipe, "checkNbt", "Z");
					m.visitVarInsn(ALOAD, 0);
					m.visitVarInsn(ILOAD, 2);
					m.visitFieldInsn(PUTFIELD, recipe, "checkNbt", "Z");
					m.visitInsn(RETURN);
					m.visitMaxs(2, 3);
					m.visitEnd();
				}
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(cw);
		return cw.toByteArray();
	}

}
