package malte0811.villagerMeta.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import malte0811.villagerMeta.client.ClientEventHandler;

public class MessageTradeSync implements IMessage {
	boolean[] nbtSensitive;
	boolean[] metaSensitive;
	public MessageTradeSync(boolean[] nbt, boolean[] meta) {
		nbtSensitive = nbt;
		metaSensitive = meta;
	}
	public MessageTradeSync() {}

	@Override
	public void fromBytes(ByteBuf buf){
		int l = buf.readInt();
		nbtSensitive = new boolean[l];
		metaSensitive = new boolean[l];
		for (int i = 0;i<l;i++) {
			nbtSensitive[i] = buf.readBoolean();
			metaSensitive[i] = buf.readBoolean();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		int l = nbtSensitive.length;
		buf.writeInt(l);
		for (int i = 0;i<l;i++) {
			buf.writeBoolean(nbtSensitive[i]);
			buf.writeBoolean(metaSensitive[i]);
		}
	}

	public static class Handler implements IMessageHandler<MessageTradeSync, IMessage> {
		@Override
		public IMessage onMessage(MessageTradeSync message, MessageContext ctx) {
			ClientEventHandler.meta = message.metaSensitive;
			ClientEventHandler.nbt = message.nbtSensitive;
			ClientEventHandler.current = null;
			return null;
		}
	}
}