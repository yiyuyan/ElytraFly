package cn.ksmcbrigade.iw;

import cn.ksmcbrigade.vmr.module.Config;
import cn.ksmcbrigade.vmr.module.Module;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class BoatModule extends Module {
    public BoatModule() throws IOException {
        super("hack.name.bf");
        JsonObject object = new JsonObject();
        object.addProperty("dm",0.3D);
        setConfig(new Config(new File("BoatFly"),object));
    }

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
        JsonElement dm = getConfig().get("dm");
        vehicle.setDeltaMovement(new Vec3(0,dm==null?0.3D:dm.getAsDouble(),0));
    }
}
