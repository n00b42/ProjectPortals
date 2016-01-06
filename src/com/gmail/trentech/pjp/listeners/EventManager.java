package com.gmail.trentech.pjp.listeners;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.IgniteEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.ConfigManager;
import com.gmail.trentech.pjp.Resource;
import com.gmail.trentech.pjp.commands.CMDTeleportUnSafe;
import com.gmail.trentech.pjp.events.TeleportEvent;

public class EventManager {

	@Listener
	public void onTeleportEvent(TeleportEvent event, @First Player player){
		Location<World> src = event.getSrc();
		Location<World> dest = event.getDest();

		if(!(player.hasPermission("pjp.worlds." + dest.getExtent().getName()) || player.hasPermission("pjw.worlds." + dest.getExtent().getName()))){
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to travel to ", dest.getExtent().getName()));
			return;
		}
		
		if(!player.setLocationSafely(dest)){
			CMDTeleportUnSafe.players.put(player, dest);
			player.sendMessage(Text.builder().color(TextColors.DARK_RED).append(Text.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/tu")).append(Text.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			return;
		}
		
		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
			Resource.spawnParticles(src, 0.5, true);
			Resource.spawnParticles(src.getRelative(Direction.UP), 0.5, true);
			
			Resource.spawnParticles(dest, 1.0, false);
			Resource.spawnParticles(dest.getRelative(Direction.UP), 1.0, false);
		}

		player.sendTitle(Title.of(Text.of(TextColors.DARK_GREEN, dest.getExtent().getName()), Text.of(TextColors.AQUA, "x: ", dest.getExtent().getSpawnLocation().getBlockX(), ", y: ", dest.getExtent().getSpawnLocation().getBlockY(),", z: ", dest.getExtent().getSpawnLocation().getBlockZ())));
	}
	
    @Listener
    public void onDamageEntityEvent(DamageEntityEvent event, @First BlockDamageSource damageSource) {
    	if(!(event.getTargetEntity() instanceof Player)) {
    		return;
    	}

        BlockSnapshot block = damageSource.getBlockSnapshot();
        
        if(!block.getState().getType().equals(BlockTypes.FLOWING_LAVA)){
        	return;
        }
        
        Location<World> location = block.getLocation().get();
        
		String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		if(new ConfigManager("portals.conf").getCuboid(locationName) == null){
			return;
		}
		
		event.setCancelled(true);
    }
    
    @Listener
    public void onIgniteEntityEvent(IgniteEntityEvent event, @First BlockDamageSource damageSource) {
    	if(!(event.getTargetEntity() instanceof Player)) {
    		return;
    	}

        BlockSnapshot block = damageSource.getBlockSnapshot();
        
        if(!block.getState().getType().equals(BlockTypes.FLOWING_LAVA)){
        	return;
        }
        
        Location<World> location = block.getLocation().get();
        
		String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		if(new ConfigManager("portals.conf").getCuboid(locationName) == null){
			return;
		}
		
		event.setCancelled(true);
    }
}
