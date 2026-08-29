package me.zombii.horizon.common.cc.blocks.peripherals.screen;

import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.world.Zone;

public class BlockEntityScreenPeripheral extends BlockEntity {

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(
                BlockScreenPeripheral.BE_ID.toString(),
                (block, zone, x, y, z) ->
                        new BlockEntityScreenPeripheral(zone, x, y, z)
        );
    }

    public BlockEntityScreenPeripheral() {
        this(null, 0, 0, 0);
    }

    public BlockEntityScreenPeripheral(Zone zone, int globalX, int globalY, int globalZ) {
        super(zone, globalX, globalY, globalZ);
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }

    @Override
    public String getBlockEntityId() {
        return BlockScreenPeripheral.BE_ID.toString();
    }

    @Override
    public boolean isTicking() {
        return false;
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
    }

}