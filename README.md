This is a small coremod for Minecraft 1.7.10 that forces villagers to check the meta- and NBT-data of the items they are buying. NBT-checking is disabled by default, but can be enabled in the config. This will still work if the mod is installed on the server but not on the client (The output item will show up on the client, but can't be removed from the slot).

It won't be necessary to update this to Minecraft 1.8.x since the bug fixed by this mod was fixed by Mojang for version 1.8.1.

Developers can include the `malte0811.villagerMeta.api` package in their mod and use `VillagerHelper.createRecipe` to create recipes/trades while specifying whether they should check NBT-/metadata if this mod is installed.