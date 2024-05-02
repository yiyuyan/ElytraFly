package cn.ksmcbrigade.iw;

import cn.ksmcbrigade.vmr.module.Module;
import cn.ksmcbrigade.vmr.uitls.ModuleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Mod(ElytraFly.MODID)
public class ElytraFly {

    public static final String MODID = "ef";

    public static Module EF = new Module("hack.name.ef") {

        private int timer;
        private final Minecraft MC = Minecraft.getInstance();

        @Override
        public void playerTick(Minecraft MC, @Nullable Player player) {
            if(player==null){
                return;
            }

            if(timer > 0)
                timer--;
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if(chest.getItem() != Items.ELYTRA)
                return;
            if(player.isFallFlying())
            {
                if(player.isInWater())
                {
                    sendStartStopPacket();
                    return;
                }
                controlSpeed();
                controlHeight();
                return;
            }
            if(ElytraItem.isFlyEnabled(chest) && MC.options.keyJump.isDown())
                doInstantFly();
        }

        private void sendStartStopPacket()
        {
            ServerboundPlayerCommandPacket packet;
            if (MC.player != null) {
                packet = new ServerboundPlayerCommandPacket(MC.player,ServerboundPlayerCommandPacket.Action.START_FALL_FLYING);
                Objects.requireNonNull(MC.getConnection()).getConnection().send(packet);
            }
        }

        private void controlHeight()
        {

            Vec3 v = null;
            if (MC.player != null) {
                v = MC.player.getViewVector(0);
            }

            if(MC.player!=null){
                if(MC.options.keyJump.isDown())
                    MC.player.setDeltaMovement(v.x, v.y + 0.08, v.z);
                else if(MC.options.keyShift.isDown())
                    MC.player.setDeltaMovement(v.x, v.y - 0.04, v.z);
            }
        }

        private void controlSpeed()
        {

            float yaw = 0;
            if (MC.player != null) {
                yaw = (float)Math.toRadians(MC.player.getUpVector(0).y);
            }
            Vec3 forward = new Vec3(-Mth.sin(yaw) * 0.05, 0,
                    Mth.cos(yaw) * 0.05);

            Vec3 v = MC.player.getViewVector(0);

            if(MC.options.keyUp.isDown())
                MC.player.setDeltaMovement(v.add(forward));
            else if(MC.options.keyDown.isDown())
                MC.player.setDeltaMovement(v.subtract(forward));
        }

        private void doInstantFly()
        {

            if(timer <= 0)
            {
                timer = 20;
                if (MC.player != null) {
                    MC.player.setJumping(false);
                    MC.player.setSprinting(true);
                    MC.player.jumpFromGround();
                }
            }

            sendStartStopPacket();
        }
    };

    public static Module BF = new Module("BoatFly") {
        @Override
        public void playerTick(Minecraft MC, @Nullable Player player) {
            if(player==null){
                return;
            }
            if(player.getVehicle()==null){
                return;
            }
            Entity vehicle = player.getVehicle();
            if(!vehicle.getType().equals(EntityType.BOAT)){
                return;
            }
            if(!MC.options.keyJump.isDown()){
                return;
            }
            vehicle.setDeltaMovement(new Vec3(0,0.3,0));
        }
    };

    public ElytraFly() {
        MinecraftForge.EVENT_BUS.register(this);
        ModuleUtils.add(EF);
        ModuleUtils.add(BF);
    }
}
