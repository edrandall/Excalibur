// Declare our name
package com.my64k.kew.excalibur;

//Include the bukkit libraries so we can do bukkit stuff
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.Effect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/////////////////////////////////////////////
//
// This is our Plugin
//
//
public final class Excalibur extends JavaPlugin 
{
	
	// Constructor
	public Excalibur()
	{
	}
	
	// Our tick schedule
	public int schedule = 0;

	// This is the name of our plugin
	static String commandName = "excalibur";
	
	// The name of excalibur
	public static final String swordName = "\u00A7l\u00A76Excalibur\u00A7r";		
		
	// The lore of excalibur
	public static final String swordLore = "This is the sword of \u00A7lKing Arthur\u00A7r.";
	    	
	// Our spawn location for the sword
	Location spawnLocation = null;
	
	// Our actual sword as an Item...
	public Item sword = null;
	
	// Player who has Excalibur
	public Player player = null;
	
	// If true then player is holding excalibur.
	public boolean holding = false; 
	
	// This is our SwordListener. It listens for events around the sword.
	SwordListener swordListener = null;
	
	// How long we should wait for
	int voteMaxTimeSeconds = 30;//60*5;
	
	// At what point our vote started
	Date voteTimeStarted = null;
	
	public enum Mode
	{
		Attract, // We are inviting players to play
		Vote,    // We are starting a vote to play
		Running, // We are running.	
	}
	
	// This is our current mode....	
	public Mode mode = Mode.Attract;
	
	// How many ticks have we run;
	long ticks = 0;
	
	
	// This code runs every time we get a timer update from minecraft.
	public void tick()
	{		
		//this.getServer().broadcastMessage("tick");
		
		
		switch(mode)
		{
			case Attract:
			{
				if ((ticks % (60L*30L)) == 0)
				{
					this.getServer().broadcastMessage("\u00A74This server is running \u00A75*" + swordName + "\u00A75*");
					this.getServer().broadcastMessage("\u00A74Written by Kew2001 and DrMiaow .");
					this.getServer().broadcastMessage("\u00A74");				
					this.getServer().broadcastMessage("\u00A74To start a game type the command.");			
					this.getServer().broadcastMessage("\u00A74\u00A7f/excalibur start");
				}
			}
			break;
			
			case Vote:
			{
				if (voteTimeLeftSeconds() < 0)
				{
					this.getServer().broadcastMessage("\u00A74Vote has expired.");
					endVote();
				}
				else				
				if (voteTimeLeftSeconds() < 10)
				{
					voteCountDown();
				}
				else
				if (voteTimeLeftSeconds() < 60)
				{
					if (voteTimeLeftSeconds() % 10 == 0)
					{
						voteCountDown();
					}
				}
				
			}
			break;		

			case Running:
			{
				this.getServer().broadcastMessage("\u00A74There is a game in progress.");
				this.getServer().broadcastMessage("\u00A74To join this game type the command.");			
				this.getServer().broadcastMessage("\u00A74\u00A7f/excalibur join");
			}
			break;		
			
		}		
	}
	
	
	
	
	// send the standard startup message to player
	public void greet(Player player)
	{
		
		player.sendMessage("\u00A74This server is running \u00A75*" + swordName + "\u00A75*");
		player.sendMessage("\u00A74Written by Kew2001 and DrMiaow .");
		player.sendMessage("\u00A74");
		
		switch(mode)
		{
			case Attract:
			{
				player.sendMessage("\u00A74To start a game type the command.");			
				player.sendMessage("\u00A74\u00A7f/excalibur start");
			}
			break;
			
			case Vote:
			{
				player.sendMessage("\u00A74There is a vote in progress to start a game.");
				player.sendMessage("\u00A74To vote to join this game type the command.");			
				player.sendMessage("\u00A74\u00A7f/excalibur vote");
			}
			break;		

			case Running:
			{
				player.sendMessage("\u00A74There is a game in progress.");
				player.sendMessage("\u00A74To join this game type the command.");			
				player.sendMessage("\u00A74\u00A7f/excalibur join");
			}
			break;		
			
		}
		
		
		player.sendMessage("\u00A74");		
	}
	
	
	
	void endVote()
	{
		mode = Mode.Attract;
	}
	
	void startVote()
	{
		
		mode = Mode.Vote;
		
		// This will record when we started the vote
		voteTimeStarted = new Date();
		
		if (schedule == 0)
		{
			schedule = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, 				
			new Runnable() 
			{			
				public void run() 
				{				  
					tick();	
					ticks++;
				}			  	
			},
			0,20);
		}
		
	}
	

	int voteDurationSeconds()
	{
		// get 'now'
		Date now = new Date();
		
		long diffMs = now.getTime() -  voteTimeStarted.getTime();
		
		return (int)(diffMs/1000);
	}
	
	int voteTimeLeftSeconds()
	{
		return voteMaxTimeSeconds - voteDurationSeconds();
	}	
	
	
	
	
	
	void voteCountDown()
	{
		this.getServer().broadcastMessage("Vote will expire in " + voteTimeLeftSeconds() + " seconds.");
	}
	
    // If this returns true the player is an op.
    // This will output a warning if the player is not an op
    void startGame(CommandSender sender)
    {
    	switch(mode)
		{
			case Attract:
			{
				
				// start the vote..
				startVote();
												
				this.getServer().broadcastMessage(sender.getName() + " has started a vote to play " + swordName + ".");
				
				// Give the countdown...
				//voteCountDown();
				
			}
			break;
			
			case Vote:
			{
				sender.sendMessage("\u00A74There is already a vote being held..");
			}
			break;		

			case Running:
			{										
				sender.sendMessage("\u00A74There is already a game running");
			}
			break;		
			
		}    	
    }	
    
    // If this returns true the player is an op.
    // This will output a warning if the player is not an op
    void status(CommandSender sender)
    {
    	switch(mode)
		{
			case Attract:
			{
				sender.sendMessage("\u00A74There is no " + swordName + "\u00A74 game running.");								
			}
			break;
			
			case Vote:
			{
				sender.sendMessage("\u00A74There is a vote being held to start a game of " + swordName + ".");
				
				if(voteTimeLeftSeconds() < 0)
				{
					sender.sendMessage("\u00A74Vote has expired.");
				}
				else 
				{
					sender.sendMessage("\u00A74Vote will expire in \u00A7f" + voteTimeLeftSeconds() + "\u00A74 seconds.");
				}
				
			}
			break;		

			case Running:
			{										
				sender.sendMessage("\u00A74There is a game of " + swordName + "\u00A74 underway.");
			}
			break;		
			
		}    	
    }	    
	
    // Report on the status of the sword
    public void Report(String name)
    {
    	String report = "";
    	
    	if (sword != null)
    	{
    		report = "Sword is an item floating in the world.";
    	}

    	if (player != null)
    	{
    		report += "Sword is being held by player '" + player.getName() + "'.";
    	}
    	
    	if (!report.isEmpty())
    	{
    		getLogger().info(name + " " +  report);
    	}
    		
    }
	
	
	// This will return true if this player is holding excalibur	
	public boolean isThisPlayerHoldingExcalibur(Player player)
	{
				
		
		if (this.sword != null)
		{
			// There is no excalibur to be held... or it is on the ground somewhere...
			return false;
		}
		
		if (this.player == null)
		{
			// no player is holding excalibur
			return false;
		}
		
			
		// if the ids match up and we are holding
		if (
				(this.player.getEntityId() == player.getEntityId()) 
				&& 
				(this.holding)
			)		
		{			
			return true;
		}
		
		return false;
		
	}
	
	// Removes excalibur from all players
	void removeExcaliburFromAllPlayers()
	{
		Player[] players = this.getServer().getOnlinePlayers();		
    	for(int p = 0;p < players.length;p++) 
    	{
    		this.removeExcaliburFromPlayer(players[p]);    	
    	}		
		
	}

	// This gets called when the plugin is enabled..
	@Override
    public void onEnable()
	{
        // TODO Insert logic to be performed when the plugin is enabled
		//getLogger().info("onEnable has been invoked!");

    	// We do this to make sure nobody has excalibur when we start up
    	removeExcaliburFromAllPlayers();
		
		// Create our sword listener...
		swordListener = new SwordListener(this);
		
		// tell server to send events to our sword listener.
		getServer().getPluginManager().registerEvents(swordListener, this);
		
		// remove excalibur - just in case
		//plugin.removeExcaliburFromPlayer(event.getPlayer());
		
		//
    	// Does this player have multiple excaliburs??
    	//
		
		
    }
	
	
 
	// This gets called when the plugin is disabled
    @Override
    public void onDisable() 
    {
        // TODO Insert logic to be performed when the plugin is disabled
    	//getLogger().info("onDisable has been invoked!");
    
    	// We do this to make sure nobody has excalibur if we disable the plugin
    	removeExcaliburFromAllPlayers();
    	
    	//PlayerInteractEvent.getHandlerList().unregister(this);
    	        
    	//HandlerList.unregisterAll(this);
    	    	
    	swordListener = null;
    }
    
    
    // Returns true if we should let the player keep the sword
    public boolean swordPickedUpByPlayer(Player player)
    {
    	// We keep track of the player who has Excalibur
    	this.player = player;
		// And we no longer track it as an Entity
		this.sword = null;
		
		// Make sure they they have not duped		
		if (checkForDupes(player,true))
		{
			
			this.getServer().getConsoleSender().sendMessage("swordPickedUpByPlayer has caused dupe...");
			
			this.removeExcaliburFromPlayer(player);
			
			// If they have - punish them
			punishPlayerForDuping(player);
			
			// return false because we don't want the user to end up with the sword.
			return false;
		}
		//else
		//{				
		//	this.getLogger().info(" Player is holding...");
		//	this.getLogger().info(player.getItemInHand().getItemMeta().getDisplayName());			
		//}
    	
    	
    	this.getServer().broadcastMessage(player.getName() + " has Excalibur.");
    	
    	return true;
    }
    
    public void swordDroppedByPlayer(Player player,Item sword)
    {
    	// Forget who has Excalibur
    	this.player = null;
    	// and start tracking the sword
    	this.sword = sword;
    	this.getServer().broadcastMessage(player.getName() + " has dropped Excalibur.");
    }

    /*
    public void swordReleasedByPlayer(Player player,Item sword)
    {
    	// Forget who has Excalibur
    	this.player = null;
    	// and start tracking the sword
    	this.sword = sword;
    	
    	this.getServer().broadcastMessage(player.getName() + " has dropped Excalibur on purpose.");
    }
    */
    
    public void swordWieldedByPlayer(Player player)
    {
    	
    	// Make sure they they have not duped		
		if (checkForDupes(player,false))
		{
			// If they have - punish them
			punishPlayerForDuping(player);
			
			return;
		}
		
    	// Player is holding Excalibur
    	holding = true;
    			
    	player.playSound(player.getLocation(), org.bukkit.Sound.ENDERDRAGON_GROWL , 10, 1);
    }
    
    public void swordUnwieldedPlayer(Player player)
    {   
    	// Player is no longer holding Excalibur
    	holding = false;
    	player.playSound(player.getLocation(), org.bukkit.Sound.CHICKEN_EGG_POP , 10, 1);
    }
    
	
	// This should be called when our sword is destroyed.
	public void swordDestroyed()
	{
		this.getServer().broadcastMessage("Excalibur has been destroyed.");
		
		this.sword = null;
		
		// So when the sword is destroyed for now 
		// all we do is respawn the sword.
		spawnSword();
	}
	    

	// This should be called when our sword is destroyed.
	public void swordSpawned(Item sword)
	{
		this.getServer().broadcastMessage("Excalibur has spawned.");
		
		this.sword = sword;
		this.player = null;
		
	}
	
	
    // This will spawn a new sword..
    public void spawnSword()
    {
    	
    	// We can only spawn if someone has set the spawn location
		if(spawnLocation == null)
		{
			// send the player a message that the spawn point is not set		
			this.getServer().getConsoleSender().sendMessage("Excalibur not spawned. Spawn point has not been set.");
			return;
		}
		
    	if(player != null)
    	{
    		this.getServer().getConsoleSender().sendMessage("Excalibur not spawned player already has a sword.");
			return;
    	}
    	
    	if(sword != null)
    	{
    		this.getServer().getConsoleSender().sendMessage("Excalibur not spawned sword already exists but no player has it.");
			return;
    	}
    	
    	// Create an itemstack of one gold sword
		ItemStack itemStack = new ItemStack(Material.GOLD_SWORD);
		
		// Add the sword to the world
		//this.spawnLocation.getWorld().dropItem(spawnLocation, itemStack);
		
		// Get the metadata
		ItemMeta x;
		x = itemStack.getItemMeta();		
		
		// Change the 'name' metadata
		x.setDisplayName(swordName);

		
		// Change the 'lore' metadata
		List<String> lore = new ArrayList<String>();		
		lore.add(swordLore);		
		x.setLore(lore);
		
		//set damage to awesome
		x.addEnchant(Enchantment.DAMAGE_ALL, 9000, true);
		x.addEnchant(Enchantment.DURABILITY, 9000, true);
		x.addEnchant(Enchantment.FIRE_ASPECT, 9000, true);
		x.addEnchant(Enchantment.KNOCKBACK, 10, true);
		x.addEnchant(Enchantment.LOOT_BONUS_MOBS, 10, true);
		
		getServer().broadcastMessage("*EXCALIBUR SPAWNED AT EXCALIBUR SPAWNPOINT*");
		
		//set metadata back into item
		itemStack.setItemMeta(x);		
		Item newSword;		
		newSword = this.spawnLocation.getWorld().dropItemNaturally(spawnLocation, itemStack);		
		newSword.setVelocity(new Vector());		
		
    }
    
    // If this returns true the player is an op.
    // This will output a warning if the player is not an op
    boolean playerMustBeOp(CommandSender sender)
    {
    	// If this is a player we need to test that it is an op 
    	if (sender instanceof Player) 
    	{   
    		// Get a reference to the player object
    		Player player = (Player)sender;
    	
    		// test that it is an op
	    	if (!player.isOp())
	    	{
	    		// Warn if it is not 
	    		player.sendMessage("You must be an op to do that.");
	    		
	    		// return false to signify that the command should not be run
	    		return false;
	    	}	    	
    	}
    	
    	// CODE GOES HERE
    	return true;        	
    }
    
    // This is where we put out setSpawn command
    void setSpawn(CommandSender sender)
    {
    	
		// /excalibur setspawn
		//
		// If the player types /excalibur setspawn then we want to set the spawn point for the sword.
		// This should only be able to be done by the console or an op.
    	
    	// This is where we will put the code that sets spawn    	
		
    	// We can only set Players on fire.. 
    	if (sender instanceof Player) 
    	{   
    		// Get a reference to the player object
    		Player player = (Player)sender;
    		
    		// Is the player an op?    		
    		if (player.isOp())
    		{     			
    			// Keep everyone informed
    			if (this.spawnLocation == null)
    			{
    				this.getServer().broadcastMessage("Spawn point for Excalibur has been set.");
    			}
    			else
    			{
    				this.getServer().broadcastMessage("Spawn point for Excalibur has been moved.");
    			}
    				
    			
    			// Set the spawn point
    			this.spawnLocation = player.getLocation();
    			
    			// Give the player who set spawn some feedback
    			player.sendMessage("Spawn point for Excalibur has been set to this location.");
    			
    			// For debugging....
    			getLogger().info("setspawn.X = " + this.spawnLocation.getX());
    			getLogger().info("setspawn.Y = " + this.spawnLocation.getY());
    			getLogger().info("setspawn.Z = " + this.spawnLocation.getZ());
    			
    			
    			spawnLocation.setPitch(0);
    			spawnLocation.setYaw(0);
    			
    			//spawnLocation.setX(spawnLocation.getX() + 1);
    			//spawnLocation.setY(spawnLocation.getY() + 1);
    			//spawnLocation.setZ(spawnLocation.getZ() + 1);
    			
    			// Drop the sword
    			//this.sword = this.spawnLocation.getWorld().spawnEntity(spawnLocation,EntityType.FIREBALL);
    			
    			
    			//Vector v = new Vector();
    			//v.zero();
    			    			
    			
    			/*
    			  //replace the sword with a fireball
    			this.sword = (Item)this.spawnLocation.getWorld().spawnEntity(spawnLocation,EntityType.FIREBALL);
    			
    			// Set as fireball so we can do fireball things to it
    			Fireball fireball = (Fireball)this.sword;    			
    			
    			// Make it super powerful
    			fireball.setYield(500f);
    		*/	
    			
    		
    		}
    	
    		
    	}
    	else
    	{        		
    		getLogger().warning("Only player ops can use setspawn.");
    	}
    }
    

    // This handles all our commands...
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {    	
    	// We only want to listen to our own command.
    	// So we make sure that we don't do anything if the command
    	// we receive is not 'excalibur'
    	if(cmd.getName().equalsIgnoreCase(commandName))
    	{     		
    		// If we get here, the command is 'excalibur'
    		
    		// handle single argument commands
    		// The user has typed /excalibur SOMETHING
    		if (args.length==1)
    		{
    			if (args[0].equalsIgnoreCase("boom"))
    			{
    				sender.sendMessage("u will go BOOM");
    			}
    			
    			else
    				
    			if (args[0].equalsIgnoreCase("setspawn"))    			
    			{
					if (playerMustBeOp(sender))
					{					    				
						this.setSpawn(sender);
					}
    			}
    			else
    			if (args[0].equalsIgnoreCase("start"))
    			{															    		
					this.startGame(sender);
    			}
    			else
    			if (args[0].equalsIgnoreCase("status"))
    			{															    		
					this.status(sender);
    			}
    			else    				
				if (args[0].equalsIgnoreCase("spawn"))
    			{
					// to use this command you must be an op.
					if (playerMustBeOp(sender))
					{					
						// so if we get here we must be an op..
						
						if(spawnLocation == null)
						{
							sender.sendMessage("Spawn point has not been set. To set the spawn point, type: /excalibur setspawn");
						}
						else
						{						
							spawnSword();
						}
					}
        		}
    				
    		}
    		    		
           return true;        	    		
    	}
    	
    	
    	
        // If this hasn't happened the a value of false will be returned.
    	return false; 
    }
    
    //this returns true if this ItemStack is a excalibur
    public boolean isItemStackExcalibur(ItemStack itemStack)
    {
    	//getLogger().info("isItemStackExcalibur");
    	
    	// get the metadata
    	ItemMeta metadata = itemStack.getItemMeta();
    	
    	/// No metadata then it can't be excalibur
    	if (metadata == null) return false;
    	
    	// No lore then it can't be excalibur
    	if ( metadata.getLore() == null) return false;
    	
    	// No name then it can't be excalibur
    	if ( metadata.getDisplayName() == null) return false;
    	
    	//getLogger().info("isItemStackExcalibur metadata.getLore().get(0) = '" + metadata.getLore().get(0) + "'");
    	//getLogger().info("isItemStackExcalibur metadata.getDisplayName() = '" + metadata.getDisplayName() + "'");
    	//getLogger().info("isItemStackExcalibur itemStack.getType() = '" + itemStack.getType() + "'");
    	
    	//this.getServer().broadcastMessage("metadata.getLore().get(0) = " + metadata.getLore().get(0));
    	// this will be true only if the lore and name match what we are looking for
    	
    	return (itemStack.getType() == Material.GOLD_SWORD) && (metadata.getLore().get(0).equals(swordLore)) && (metadata.getDisplayName().equals(swordName));
    }
    
    // This returns true if this Entity is an excalibur
    public boolean isItemExcalibur(Item item)
    {
    	//getLogger().info("isItemExcalibur");
    	
		// Now we can use the itemstack comparison
		return isItemStackExcalibur(item.getItemStack());
    	
    }
    
    // This returns true if this Entity is an excalibur
    public boolean isEntityExcalibur(Entity entity)
    {    	
    	//getLogger().info("isEntityExcalibur");
    	
    	if ( entity instanceof Item )
    	{    		
    		// Now we can use the itemstack comparison
    		return isItemExcalibur((Item) entity);
    		
    	}
    	else
    	{
    		// It must be an item to be Excalibur, so it can't be Excalibur.. 
    		return false;
    	}
    	
    }
    

        
    
    // This will ensure you don't have a duped excalibur.
    // This gets called when a user picks up or wields excalibur
    // Returns true if we detected a dupe that was removed.
    public boolean checkForDupes(Player player, boolean pickingUp)
    {
    	//this.getServer().broadcastMessage("checkForDupes " + player.getName());
    	
    	// This called if
    	// 1) The player picked up excalibur
    	// 2) The player switched to using excalibur    	
    	if ((this.player == null) && (this.sword == null))
    	{
    		if (removeExcaliburFromPlayer(player))
    		{
    	    	
    			this.getServer().broadcastMessage("removeExcaliburFromPlayer says we found one..");
    		}
    	}
    	
    	// If we have the sword and the sword is floating - then this is impossible
    	if ((this.player != null) && (this.sword != null))
    	{
    		if (this.player.getEntityId() != player.getEntityId())
    		{
    			this.getServer().broadcastMessage("hey! " + this.player.getName() + " already has the sword");
    		}
    		
    		//removeExcaliburFromPlayer(player);
    		//removeExcaliburFromPlayer(this.player);
    	}
    	
    	
    	int excaliburs = 0;
    	
    	//
    	// Does this player have multiple excaliburs??
    	//
    	for(Iterator<ItemStack> iterator = player.getInventory().iterator(); iterator.hasNext(); ) 
    	{   
    		// get the next item from their inventory
    		ItemStack itemStack = iterator.next();
    		
    		// if it is not null
	        if ((itemStack != null) && (isItemStackExcalibur(itemStack)))
	        {
	        	// print it..
	    		//this.getServer().broadcastMessage("EXCALIBUR " + itemStack.toString());
	    		excaliburs++;
	    	}	        
    	}
    	
    	
    	if (
    			// If we are not picking up, then more than 1 is bad
    			(excaliburs > 1) 
    			|| 
    			// If we *are* picking up then 1 or more is bad
    			(pickingUp && (excaliburs > 0)))
    	{
    		removeExcaliburFromPlayer(player);    		
    		this.getServer().broadcastMessage("DUPE DETECTED");
    		return true;    		
    	}
    	
    	
    	return false;
    }
    
    
    // Remove all instances of Excalibur from the player
    // return true if we removed any Excaliburs
    // We use this to enforce no dupes.
    public boolean removeExcaliburFromPlayer(Player player)
    {
    	this.getServer().broadcastMessage("removeExcaliburFromPlayer " + player.getName());
    	
    	// This is true of we removed an Excalibur
    	boolean weRemovedExcalibur = false;
    	
    	//
    	// Look at the inventory and remove any Excaliburs we see 
    	//
    	for(Iterator<ItemStack> iterator = player.getInventory().iterator(); iterator.hasNext(); ) 
    	{   
    		// get the next item from their inventory
    		ItemStack itemStack = iterator.next();
    		
    		// if it is not null
	        if ((itemStack != null) && (isItemStackExcalibur(itemStack)))
	        {
	        	player.getInventory().remove(itemStack);
	        	weRemovedExcalibur = true;
	    	}	        
    	}
    
    	
    	if (weRemovedExcalibur)
    	{
	    	//
	    	// After removing excalibur from player we need to tidy up the universe
	    	//
	    	    	
	    	// if we think a player has the sword..
	    	if (this.player != null)
	    	{
	    		// If it is the player we just removed excalibur from...
		    	if(player.getEntityId() == this.player.getEntityId())
		    	{
		    		// then we stop tracking them as the player that has excalibur
		    		this.player = null;
		    	}
	    	}	
    	}
    	
    	return weRemovedExcalibur;
    }
    
    //this is used to punish players for duping
    public void punishPlayerForDuping(Player player)
    {
    	this.getServer().broadcastMessage("punishPlayerForDuping " + player.getName());
    	
    	player.getWorld().strikeLightning(player.getLocation());
    
    }

}
