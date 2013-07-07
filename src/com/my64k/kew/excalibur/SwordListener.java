// Declare our name
package com.my64k.kew.excalibur;

import java.util.Iterator;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;



 
public class SwordListener implements Listener {
	
	
	// A reference to our plugin
	Excalibur plugin;
	
	// This is our constructor.
	// This gets called when our class gets construructed.
	// Like when someone uses "new SwordListener(plugin)"
	public SwordListener(Excalibur plugin)
	{
		// save a reference back to our plugin so we can get access to all the plugins stuff. 
		this.plugin = plugin;		
	}
	
	@EventHandler
    public void playerJoinedServer(PlayerJoinEvent event) 
	{    	
		this.plugin.getLogger().info("Player " + event.getPlayer().getName() + " has joined server.");
		
		// remove excalibur - just in case
		plugin.removeExcaliburFromPlayer(event.getPlayer());
		
		// send the standard greeting
		plugin.greet(event.getPlayer());
		
    }    	

	@EventHandler
    public void playerLeftServer(PlayerQuitEvent event) 
	{
		//plugin.Report("playerLeftServer- enter");
		
		this.plugin.getLogger().info("Player " + event.getPlayer().getName() + " has left server."); 	
		
		// If a player has excalibur...
		if (plugin.player != null)
		{
		
			if (plugin.player.getEntityId() ==  event.getPlayer().getEntityId())
			{
				plugin.removeExcaliburFromPlayer(event.getPlayer());
				//plugin.swordDroppedByPlayer(event.getPlayer(), plugin.sword);
				
				plugin.sword = null;
				plugin.player = null;
				
				event.getPlayer().getServer().broadcastMessage("*PERSON HOLDING EXCALIBUR LEFT - IT WILL RESPAWN*");
				
				plugin.spawnSword();
			}
		}	
		
		//plugin.Report("playerLeftServer- exit");
    }    	

	@EventHandler
    public void itemSpawn(ItemSpawnEvent event) 
	{
		//plugin.Report("itemSpawn- enter");

		// If the item that just spawned is excalibur
		if (this.plugin.isItemStackExcalibur(event.getEntity().getItemStack()))
        {
        	plugin.getServer().broadcastMessage("*EXCALIBUR SPAWNED*");
        	
        	if (plugin.player != null)
        	{        	
        		plugin.getServer().broadcastMessage("*EXCALIBUR DROPPED BY PLAYER*");
        		// The sword we find needs to be passed in as plugin.sword
        		plugin.swordDroppedByPlayer(plugin.player, event.getEntity());
        	}
        	else
        	{
        		plugin.getServer().broadcastMessage("*EXCALIBUR SPAWNED WITH NO PLAYER*");
        		plugin.swordSpawned(event.getEntity());
        	}
        	
        }
		
		//plugin.Report("itemSpawn- exit");
	}
	
	
	
	@EventHandler
    public void playerDied(EntityDeathEvent event) 
	{
		//plugin.Report("playerDied- enter");
		
		// TODO: Find excalibur in event.getDrops()
		
		ItemStack droppedExcalibur = null;
		
		
		
    	//
    	// Look at the inventory and remove any Excaliburs we see 
    	//
    	for(Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext(); ) 
    	{   
    		// get the next item from their inventory
    		ItemStack itemStack = iterator.next();
    		
    		// if it is not null
	        if ((itemStack != null) && (plugin.isItemStackExcalibur(itemStack)))
	        {
	        	plugin.getServer().broadcastMessage("*SOMETHING DIED AND DROPPED EXCALIBUR*");
	        	
	        	if (droppedExcalibur != null)
	        	{
	        		this.plugin.getLogger().warning("MORE THAN ONE EXCALIBUR DROPPED AT ONCE");
	        	}
	        	else
	        	{	        	
 	        	    // We have excalibur
	        		droppedExcalibur = itemStack;
	        	}
	    	}	        
    	}		
		
    	
    	// If we detected a dropped excalibur
    	if (droppedExcalibur != null)
    	{
		
			// The entity that died, is it a player?  		
			if (event.getEntity() instanceof Player )
			{
				Player player = (Player) event.getEntity();
				
				//this.plugin.getLogger().info("Player " + player + " has left died.");
				
	
				// if we are tracking a player...
				if (plugin.player != null)
				{
				
					// and these are the same
					if (plugin.player.getEntityId() ==  player.getEntityId())
					{
						
						player.getServer().broadcastMessage("*PLAYER '" + player.getName() + "' HOLDING EXCALIBUR HAS DIED AND DROPPED IT*");
						
					}
				}
			}		
    	}
    	
    	//plugin.Report("playerDied- exit");
    }    	
	

    @EventHandler
    public void swordCombusted(EntityCombustEvent event) {
    	    	
    	//this.plugin.getLogger().info("Something has combusted?" + event.getEntity().getUniqueId());    	
    	//this.plugin.getLogger().info("This entity has combusted " + event.getEntity().getEntityId());    	
    	
    	/*
    	if (plugin.isEntityExcalibur(event.getEntity()))
    	{
    		plugin.getServer().broadcastMessage("*EXCALIBUR IS COMBUSTING*");
    		
	    	if (plugin.sword == null)	    	
	    	{
	    		plugin.getServer().broadcastMessage("plugin.sword IS NULL!!!");
	    	}
    		
	    	if (plugin.sword.getEntityId() !=  event.getEntity().getEntityId())	    	
	    	{
	    		plugin.getServer().broadcastMessage("NOT THE SWORD WE ARE TRACKING");
	    	}
    		
    		plugin.swordDestroyed();
    	}    	
    	else*/
    	// We only care and it is only safe, if we have a sword in play..
    	if (plugin.sword != null)
    	{    	
	    	//this.plugin.getLogger().info("Our sword is id " + plugin.sword.getEntityId());
	    	
	    	// If the sword and the thing being combusted are the same.. 
	    	if (plugin.sword.getEntityId() ==  event.getEntity().getEntityId())	    	
	    	{    	
	    		plugin.getServer().broadcastMessage("*EXCALIBUR IS COMBUSTING*");
	    		
	    		
	    		// The entity this event is about, the entity that combusted
	    		// has the same id as our sword... so it MUST be out sword.
	    		plugin.swordDestroyed();
	    	}
    	}
    }    
    
    @EventHandler
    public void swordDespawn(ItemDespawnEvent event) {
    	
    	plugin.Report("swordDespawn- enter");
    	
    	this.plugin.getLogger().info("Something has despawned? " + event.getEntity().getUniqueId());    	
    	this.plugin.getLogger().info("This entity has despawned " + event.getEntity().getEntityId());    	    	    	
    	this.plugin.getLogger().info("This entity has despawned " + event.getEntity().getMetadata("name"));
    	

    	// We only care and it is only safe, if we have a sword in play..
    	if (plugin.sword != null)
    	{    	
	    	//this.plugin.getLogger().info("Our sword is id " + plugin.sword.getEntityId());
	    	
	    	// If the sword and the thing being combusted are the same.. 
	    	if (plugin.sword.getEntityId() ==  event.getEntity().getEntityId())	    	
	    	{    	
	    		//this.plugin.getLogger().info("Our sword is id " + plugin.sword.getEntityId() + " HAS DESPAWNED");
	    		
	    		// The entity this event is about, the entity that combusted
	    		// has the same id as our sword... so it MUST be out sword.
	    		plugin.swordDestroyed();
	    	}
    	}
    	
    	//plugin.Report("swordDespawn- exit");
    }
    
    
    @EventHandler
    public void swordDamage(EntityDamageEvent event) {
    	
    	//plugin.Report("swordDamage- enter");
    	
    	//this.plugin.getLogger().info("This entity has damaged " + event.getEntity().getEntityId());    	    	    	
    	
    	//if (plugin.isEntityExcalibur(event.getEntity()))
    	//{
    	//	plugin.getServer().broadcastMessage("*EXCALIBUR IS DAMAGED*");
    	//	plugin.swordDestroyed();
    	//}    	
    	//else    	
    	// We only care and it is only safe, if we have a sword in play..
    	if (plugin.sword != null)
    	{    	
	    	//this.plugin.getLogger().info("Our sword is id " + plugin.sword.getEntityId());
	    	
	    	// If the sword and the thing being combusted are the same.. 
	    	if (plugin.sword.getEntityId() ==  event.getEntity().getEntityId())	    	
	    	{
	    		plugin.getServer().broadcastMessage("*EXCALIBUR IS DAMAGED1*");
	    		
	    		
	    		// The entity this event is about, the entity that combusted
	    		// has the same id as our sword... so it MUST be out sword.
	    		plugin.swordDestroyed();
	    		return;
	    	}
	    	
	    	//
	    	// Let's test it this way...
	    	//
	    	
	    	if (plugin.isEntityExcalibur(event.getEntity()))
	    	{
	    		plugin.getServer().broadcastMessage("*EXCALIBUR IS DAMAGED2*");
	    		
	    		plugin.swordDestroyed();
	    		return;
	    	}
	    	
    	}
    	
    	//plugin.Report("swordDamage- exit");
    }    
    
    
    @EventHandler
    public void swordDamageByBlock(EntityDamageByBlockEvent event) {
    	
    	    	
    	//this.plugin.getLogger().info("This entity has damagedbb " + event.getEntity().getEntityId());    	    	    	
    	//this.plugin.getLogger().info("This entity has damagedbb " + event.getEntity().getMetadata("name"));
  
    	    	
    	// We only care and it is only safe, if we have a sword in play..
    	if (plugin.sword != null)
    	{    	
	    	
	    	
	    	// If the sword and the thing being combusted are the same.. 
	    	if (plugin.sword.getEntityId() ==  event.getEntity().getEntityId())	    	
	    	{
	    		plugin.getServer().broadcastMessage("*EXCALIBUR IS DAMAGED BY BLOCK*");
	    		
	    		// The entity this event is about, the entity that combusted
	    		// has the same id as our sword... so it MUST be out sword.
	    		plugin.swordDestroyed();
	    	}
    	}
    }    
    
    @EventHandler
    public void swordDamageByEntity(EntityDamageByEntityEvent event) {
    	
    	//this.plugin.getLogger().info("Something has damaged " + event.getEntity().getUniqueId());    	
    	//this.plugin.getLogger().info("damage to " + event.getEntity().getEntityId());
    	//this.plugin.getLogger().info("damage to " + event.getEntity().getType());
    	//this.plugin.getLogger().info("damage by " + event.getDamager().getEntityId());
    	//this.plugin.getLogger().info("damage by " + event.getDamager().getType());
    	
    	// If the sword and the thing being combusted are the same.. 
    	if ((plugin.sword != null) && (plugin.sword.getEntityId() ==  event.getEntity().getEntityId()))	    	
    	{
    		plugin.getServer().broadcastMessage("*EXCALIBUR IS DAMAGED BY ENTITY???*");
    		//plugin.swordDestroyed();
    	}
    	else    	            		
		// is this PvP?    		
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player )
		{
			this.plugin.getLogger().info("PVP!");
			
			Player damager = (Player) event.getDamager();
			Player damagee = (Player) event.getEntity();
		
			// 	if the damager is the player holding excalibur.. then we want to know about it
		
			if (plugin.isThisPlayerHoldingExcalibur(damager))
			{
				
				plugin.getServer().broadcastMessage("*EXCALIBUR WHACKED " + damagee.getName() + "*");

				// if we are too close to excalibur spawn... then no damage.
				double distanceFromExcaliburSpawn = damager.getLocation().distance(plugin.spawnLocation);				
				//this.plugin.getLogger().info("distanceFromExcaliburSpawn " + distanceFromExcaliburSpawn);
				
				// if we are too close to excalibur spawn... then no damage.
				double distanceFromWorldSpawn = damager.getLocation().distance(damager.getWorld().getSpawnLocation());				
				//this.plugin.getLogger().info("distanceFromWorldSpawn " + distanceFromWorldSpawn);

				// Are we too close to the world spawn?
				if (distanceFromExcaliburSpawn < 20)
				{
					damager.sendMessage("You are too close to Excalibur spawn to harm " + damagee.getName() + " with Excalibur.");
					damagee.sendMessage("You are too close to Excalibur spawn to be harmed by " + damager.getName() + " with Excalibur.");
					event.setCancelled(true);					
				}
				else
				// Are we too close to the world spawn?
				if (distanceFromWorldSpawn < 20)
				{
					damager.sendMessage("You are too close to spawn to harm " + damagee.getName() + " with Excalibur.");
					damagee.sendMessage("You are too close to spawn to be harmed by " + damager.getName() + " with Excalibur.");
					event.setCancelled(true);
				}
				
				// if we are too close to world spawn... then no damage.
				
				//event.setCancelled(true);
			}
		
		
		}
		
		
		/*
    	//this.plugin.getLogger().info("Our sword is id " + plugin.sword.getEntityId());
    	
    	// If the sword and the thing being combusted are the same.. 
    	if (plugin.sword.getEntityId() ==  event.getEntity().getEntityId())	    	
    	{ 
    		this.plugin.getLogger().info("Our sword of id " + plugin.sword.getEntityId() + " IS DAMAGED BY ITEM");
    		
    		
    		// The entity this event is about, the entity that combusted
    		// has the same id as our sword... so it MUST be out sword.
    		plugin.swordDestroyed();
    	}
    	
    	// If the sword and the thing being combusted are the same.. 
    	if (plugin.sword.getEntityId() ==  event.getDamager().getEntityId())	    	
    	{ 
    		this.plugin.getLogger().info("Our sword of id " + plugin.sword.getEntityId() + " IS DAMAGER!");
    		
    		
    		// The entity this event is about, the entity that combusted
    		// has the same id as our sword... so it MUST be out sword.
    		//plugin.swordDestroyed();
    	}
    	*/
    }  



    
    @EventHandler
    public void swordPortal(EntityPortalEnterEvent event) {
    	
    	    	
    	//this.plugin.getLogger().info("This entity has damagedbe " + event.getEntity().getEntityId());    	    	    
    	//this.plugin.getLogger().info("This entity has damagedbe " + event.getEntity().getMetadata("name"));
    	

    	// We only care and it is only safe, if we have a sword in play..
    	if (plugin.sword != null)
    	{    	
	    	//this.plugin.getLogger().info("PORTAL Our sword is id " + plugin.sword.getEntityId() + " " + event.getEntityType() + " " + event.getEventName() );
	    		    	
	    	// If the sword and the thing being combusted are the same.. 
	    	if (plugin.sword.getEntityId() ==  event.getEntity().getEntityId())	    	
	    	{    	
	    		
	    		plugin.getServer().broadcastMessage("*EXCALIBUR THROWN INTO PORTAL*");
	    		
	    		// The entity this event is about, the entity that combusted
	    		// has the same id as our sword... so it MUST be out sword.
	    		plugin.swordDestroyed();
	    	}
    	}
    }  
    
    
    /*
    @EventHandler
    public void swordExplode(EntityExplodeEvent event) {
    	
    	//this.plugin.getLogger().info("Something has exploded " + event.getEntity().getUniqueId());    	
    	//this.plugin.getLogger().info("This entity has exploded " + event.getEntity().getEntityId());    	
    	    	
    	
    	// We only care and it is only safe, if we have a sword in play..
    	if (plugin.sword != null)
    	{    	
	    	//this.plugin.getLogger().info("Our sword is id " + plugin.sword.getEntityId());
	    	
	    	// If the sword and the thing being combusted are the same.. 
	    	if (plugin.sword.getEntityId() ==  event.getEntity().getEntityId())	    	
	    	{ 
	    		//this.plugin.getLogger().info("Our sword of id " + plugin.sword.getEntityId() + " HAS EXPLODED");
	    		
	    		plugin.getServer().broadcastMessage("*EXCALIBUR EXPLODED*");
	    		
	    		// The entity this event is about, the entity that combusted
	    		// has the same id as our sword... so it MUST be out sword.
	    		plugin.swordDestroyed();
	    	}
    	}
    }  
    */

    
    @EventHandler
    public void swordPickedUp(PlayerPickupItemEvent event) {
    		
    	//plugin.Report("swordPickedUp- enter");
    	
    	//this.plugin.getLogger().info("swordPickedUp");
    	
    	// We only care and it is only safe, if we have a sword in play..
    	if (plugin.sword != null)
    	{    	
	    	//this.plugin.getLogger().info("Our sword is id " + plugin.sword.getEntityId());
	    	
	    	// If the sword and the thing being picked up are the same.. 
	    	if (plugin.sword.getEntityId() ==  event.getItem().getEntityId())	    	
	    	{    	
	    		
	    		plugin.getServer().broadcastMessage("*EXCALIBUR PICKED UP*");
	    		//this.plugin.getLogger().info("Our sword has been picked up.");
	    		
	    		//
	    		// Sword turns from an item to an itemstack
	    		//
	    		
	    		// Tell the plugin the sword has been picked up
	    		if (!this.plugin.swordPickedUpByPlayer(event.getPlayer()))
	    		{
	    			// If we are not allowed to then we cancel this..
	    			event.setCancelled(true);	    			
	    		}
	    		
	    		
	    		if (plugin.isThisPlayerHoldingExcalibur(event.getPlayer()))
	    		{
	    			plugin.getServer().broadcastMessage("*PLAYER HAS EXCALIBUR*");
	    				    			
	    			
	    			if (plugin.isItemStackExcalibur(event.getPlayer().getInventory().getItemInHand()))
	    			{
	    				plugin.getServer().broadcastMessage("*PLAYER HOLDING EXCALIBUR*");
	    				
	    				this.plugin.swordWieldedByPlayer(event.getPlayer());
	    			}
	    			
	    		}
	    		
	    	}	    		    		    		    	
    	}
    	else
    	if (plugin.isItemExcalibur(event.getItem()))
    	{
    		this.plugin.getLogger().info("An excalibur has been picked up, but it is not the one we are tracking.");
    		
    		plugin.removeExcaliburFromPlayer(event.getPlayer());
    		
			plugin.punishPlayerForDuping(event.getPlayer());
    		
			event.setCancelled(true);    			
    		
    	}
    	
    	//plugin.Report("swordPickedUp- exit");
    }    
    
    /*
    @EventHandler
    public void swordReleased(PlayerDropItemEvent event) {
	
    	//plugin.Report("swordReleased- enter");
    	
    	// We only care and it is only safe, if we have a sword in play..
    	if (plugin.sword != null)
    	{    	
	    	//this.plugin.getLogger().info("Our sword is id " + plugin.sword.getEntityId());
	    	
	    	// If the sword and the thing being picked up are the same.. 
	    	if (plugin.sword.getEntityId() ==  event.getItemDrop().getEntityId())	    	
	    	{
	    		plugin.getServer().broadcastMessage("*EXCALIBUR RELEASED*");
	    		
	    		this.plugin.swordReleasedByPlayer(event.getPlayer(),event.getItemDrop());
	    	}
    	}
	    	
    	
    	//plugin.Report("swordDropped- exit");
    	
    }  
    */
    /*
    @EventHandler
    public void swordInventoryPickup(InventoryPickupItemEvent event) {
    		this.plugin.getLogger().info("InventoryPickupItemEvent  " + event.getItem().getEntityId());
    		
    		// is this item excalibur?
        	if (plugin.isItemStackExcalibur(event.getItem().getItemStack()))
        	{
        		plugin.getServer().broadcastMessage("*EXCALIBUR INVENTORY PICKUP*");
        	}
    }

    
    @EventHandler
    public void swordInventoryMove(InventoryMoveItemEvent event) {
    		this.plugin.getLogger().info("InventoryMoveItemEvent  " + event.getItem().getType());
    		
    		// is this item excalibur?
        	if (plugin.isItemStackExcalibur(event.getItem()))
        	{
        		plugin.getServer().broadcastMessage("*EXCALIBUR INVENTORY PICKUP*");
        	}
    }
    */
    
	    
    @EventHandler
    public void swordHeld(PlayerItemHeldEvent event) {
	
    	
    	int newSlot = event.getNewSlot();
    	int previousSlot = event.getPreviousSlot();
    	
    	//this.plugin.getLogger().info("Player has switched slots from  " + previousSlot + " to " + newSlot);
    	//this.plugin.getLogger().info("This entity has been held " + event.getItemDrop().getEntityId());
    	//this.plugin.getLogger().info("This entity(itemstack) has been held " + event.getItemDrop().getItemStack().toString());
    	//this.plugin.getLogger().info("This entity(itemstack.hashcode) has been held " + event.getItemDrop().getItemStack().hashCode());
    	
    	// Are we no longer holding excalibur?
    	
    	
    	ItemStack previousSlotItemStack = event.getPlayer().getInventory().getItem(previousSlot);
    	if (previousSlotItemStack != null)
    	{
    		//this.plugin.getLogger().info("previousSlotItemStack = " + previousSlotItemStack.toString());
    		//this.plugin.getLogger().info("previousSlotItemStack.getHashCode() " + previousSlotItemStack.hashCode());
    		
    		// is this item excalibur?
        	if (plugin.isItemStackExcalibur(previousSlotItemStack))
        	{
        		
        		// TODO: should you be wielding excalibur
        		if (plugin.sword != null)
        		{
        			// Sword is still floating...
        			this.plugin.getLogger().info("This player should not have excalibur");
        			
        			this.plugin.removeExcaliburFromPlayer(event.getPlayer());
        			
        			this.plugin.punishPlayerForDuping(event.getPlayer());        			
        			
        			return;
        		}
        		
        		// TODO: should you be wielding excalibur
        		if (plugin.player == null)
        		{
        			// Sword is still floating...
        			this.plugin.getLogger().info("This player should not have excalibur (because no player does)");
        			
        			this.plugin.removeExcaliburFromPlayer(event.getPlayer());
        			
        			this.plugin.punishPlayerForDuping(event.getPlayer());        			

        		}
        		        		
        		
        		this.plugin.getLogger().info("YOU NO LONGER WIELD EXCALIBUR");
        		
        		this.plugin.swordUnwieldedPlayer(event.getPlayer());
        		
        		
        		
        	}
    		
    	}
    	
    	ItemStack newSlotItemStack = event.getPlayer().getInventory().getItem(newSlot);
    	if (newSlotItemStack != null)    
    	{
    		//this.plugin.getLogger().info("newSlotItemStack = " + newSlotItemStack.toString());    	 	
        	//this.plugin.getLogger().info("newSlotItemStack.getHashCode() " + newSlotItemStack.hashCode());
        	
        	if (plugin.isItemStackExcalibur(newSlotItemStack))
        	{
        		// TODO: should you be wielding excalibur
        		if (plugin.sword != null)
        		{
        			// Sword is still floating...
        			this.plugin.getLogger().info("This player should not have excalibur");
        			
        			this.plugin.removeExcaliburFromPlayer(event.getPlayer());
        			
        			this.plugin.punishPlayerForDuping(event.getPlayer());        			
        			
        			return;
        		}
        		
        		// TODO: should you be wielding excalibur
        		if (plugin.player == null)
        		{
        			// Sword is still floating...
        			this.plugin.getLogger().info("This player should not have excalibur (because no player does)");
        			
        			this.plugin.removeExcaliburFromPlayer(event.getPlayer());
        			
        			this.plugin.punishPlayerForDuping(event.getPlayer());        			

        		}
        		
        		this.plugin.getLogger().info("YOU NOW WIELD EXCALIBUR");
        		
        		this.plugin.swordWieldedByPlayer(event.getPlayer());
        	}
    	}
    	
    }  
    
}