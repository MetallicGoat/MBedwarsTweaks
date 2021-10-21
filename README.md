
# MBedwarsTweaks
Tweks & Features for MBedWars

[**Support**](https://discord.gg/RC2mtdcEdD)

**Features**

 1. AdvancedSwords (Upgrades, AntiDrop and much more)
 2. Automatic Igniton of TNT
 3. Fireball Block Whitelist
 4. Buy Message
 5. Final Kill Message
 6. Team Eliminated Message
 7. Top Killers Message
 8. Heigth Cap Message 
 9. Villagers Look at You
 10. Permanent Effects
 11. Waterflow Fix
 12. Spawner Upgrades
 13. Scheudule Bed Break
 14. Disable unused Gens
 15. And More!

**Example Configuration**
 

    ############### PREVENT WATER BUILDUP ###############
    
    #Prevents liquids from going outside BW arena
    #Will not affect other gamemodes
    Prevent-Liquid-Build-Up: true
    
    ############### HEIGHT CAP ###############
    
    #Add a height cap for specific MBedwars arenas
    #Add height cap like 'arenaName:70'
    #To disable the message, set to ''
    Height-Cap:
      Enabled: false
      Message: '&cYou cannot build any higher'
      Arenas:
        - ''
    
    ############### FINAL KILL [BETA] ###############
    
    #Adds FINAL KILL to the end of a message if the kill is final
    #WARNING: May cause an error. We are looking into a fix, please report any errors in our discord
    Final-Kill-Message: false
    
    #Players get a lightning blot at their location of death if it is a final kill
    Final-Kill-Strike:
      Enabled: false
    
    ############### FIREBALL BLOCK WHITELIST ###############
    
    #Blocks that fireballs will not destroy (Overrides MBedwars' BlackList)
    FireballWhitelist:
      Enabled: true
      Blocks:
        - ENDER_STONE
        - END_STONE
    
    ############### BUY-MESSAGE ###############
    
    #Message sent to players when they purchase an item
    #Placeholders: {amount} {product}
    Buy-Message:
      Enabled: true
      Message: '&aYou Purchased &6{product} x{amount}'
    
    ############### DISABLE EMPTY BASE GENERATORS ###############
    
    #Disable generators in empty bases
    #Range = distance from team spawn to spawner
    Disable-Unused-Gens:
      Enabled: false
      Range: 6
      Gen-Types:
        - 'IRON_INGOT'
        - 'GOLD_INGOT'
    
    ############### GEN TIERS [BETA] ###############
    
    #Teirs can be configured in the gen-tiers.yml
    Gen-Tiers-Enabled: false
    
    #Override MBedwars Holo's
    Gen-Tiers-Holos-Enabled: true
    
    #If set to true, the papi placeholders on the
    #Scoreboard will update every 5 seconds
    Scoreboard-Updating: false
    
    #Adds 'Tier I' to spawners listed
    Tier-One-Titles:
      Tier-Name: '&eTier &cI'
      Spawners:
        - 'EMERALD'
        - 'DIAMOND'
    
    #Message shown above spawners
    # {spawner} {spawner-color} {time} {tier}
    Spawner-Title:
      - '{tier}'
      - '{spawner-color}{spawner}'
      - '&eSpawning in &c{time} &eseconds!'
    
    #PAPI Placeholder: %tweaks_next-tier%
    # {next-tier} {time}
    Next-Tier-Placeholder: '{next-tier} in &a{time}'
    
    ############### BED BREAK ###############
    
    #Message displayed when any bed is broken (to everyone in the arena)
    Player-Destroy-Message:
      - ''
      - '&f&lBED DESTRUCTION > {team-color}{team-name} Bed &7was destroyed by {destroyer-color}{destroyer-name}'
      - ''
    
    #Chat message displayed on Auto-Bed-Break
    Auto-Destroy-Message:
      - ''
      - '&c&lALL BEDS HAVE BEEN DESTROYED'
      - ''
    
    # https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
    Bed-Destroy-Sound: 'ENTITY_ENDER_DRAGON_GROWL'
    
    #Title displayed to a player when his bed gets destroyed
    #This replaces MBedwars' message, and only gets sent to the player with no bed
    Notification:
      Big-Title: '&cBED DESTROYED'
      Small-Title: '&fYou will no longer respawn!'
    
    ############### TEAM ELIMINATE ###############
    
    Team-Eliminate-Message-Enabled: true
    
    #Message displayed when any team is eliminated (to everyone in the arena)
    Team-Eliminate-Message:
      - ''
      - '&f&lTEAM ELIMINATED > {team-color}{team-name} Team &chas been eliminated!'
      - ''
    
    
    ############### TNT AUTO IGNITE ###############
    
    #WARNING!!! If you enable this MAKE SURE MBedwars' tnt-autoignite is FALSE
    TNT:
      Auto-Ignite: false
      Delay: 4
    
    ############### REMOVE ON USE ###############
    Empty-Buckets: true
    Empty-Potions: true
    
    ############### TOP KILLER MESSAGE [BETA] ###############
    Top-Killer-Message-Enabled: true
    
    #No Top killers FinalKill
    No-Top-Killers-Message:
      - ''
    
    # PLACEHOLDERS:
    # %Killer-1-Name% %Killer-2-Name% %Killer-3-Name%
    # %Killer-1-Amount% %Killer-2-Amount% %Killer-3-Amount%
    Top-Killer-Message:
      - '&a&l-------------------------------'
      - '                &lBedWars'
      - ''
      - '    &e&l1st Killer &7- %Killer-1-Name% - %Killer-1-Amount%'
      - '    &6&l2nd Killer &7- %Killer-2-Name% - %Killer-2-Amount%'
      - '    &c&l3rd Killer &7- %Killer-3-Name% - %Killer-3-Amount%'
      - ''
      - '&a&l-------------------------------'
    
    
    ############### REMOVE INVIS ON DAMAGE ###############
    
    #Removes invis on specified damage causes
    #I recommend you use this in combination with the Better Invisibility plugin.
    #UPDATE: Better Invisibility may have this built in now, but this still works
    Break-Invis:
      Enabled: true
      Causes:
        - ENTITY_ATTACK
        - BLOCK_EXPLOSION
        - ENTITY_EXPLOSION
    
    ############### Permanent Potion Effects [BETA] ###############
    
    #ArenaName:PotionEffectName:amplifier
    #Default amplifier is 1
    #All arenas = ALL-ARENAS
    Permanent-Effects:
      - 'Ruins:NIGHT_VISION'
    
    ############### Friendly Villagers [BETA] ###############
    
    #If this is enabled, MBedwars dealers/upgrade-dealers
    #will look at the closest players
    Friendly-Villagers: false
    
    ############### Shorten-Countdown [BETA] ###############
    
    #Shortens lobby time to a specified value
    #if arena filled over a certain percent
    Shorten-Countdown:
      Enabled: false
      Minimum-Percent-Filled: 80
      Shorten-Time-To: 10
    
    ############### PLAYER COUNT PLACEHOLDER ###############
    #Whether to count Spectators for the Player Count Placeholders
    Player-Count-Placeholder-Count-Spectators: true

**Example Gen Tiers Configuration**

    # MAKE SURE Gen-Tiers are enabled in the config.yml!
    
    # ORDER IN THE WAY YOU WANT YOUR GENS TO UPDATE
    # Tier section namee doesnt matter as long as it is unique
    # With the exception to 'bed-break', and 'game-over'
    
    # TierName (Used in PAPI Placeholders)
    # Type - Type of generator that will update (Use Spigot Item Name)
    # Time - Time until gen updates (Minutes)
    # Speed - How often an item drops (Seconds)
    # Chat - FinalKill Displayed on Gen Update (Supports Colour Codes)
    Gen-Tiers:
      1:
        TierName: 'Diamond II'
        TierLevel: '&eTier &cII'
        Type: 'diamond'
        Time: 6
        Speed: 23
        Chat: '&bDiamond Generators &ehave been upgraded to Tier &4II'
      2:
        TierName: 'Emerald II'
        TierLevel: '&eTier &cII'
        Type: 'emerald'
        Time: 6
        Speed: 40
        Chat: '&aEmerald Generators &ehave been upgraded to Tier &4II'
      3:
        TierName: 'Diamond III'
        TierLevel: '&eTier &cIII'
        Type: 'diamond'
        Time: 6
        Speed: 13
        Chat: '&bDiamond Generators &ehave been upgraded to Tier &4III'
      4:
        TierName: 'Emerald III'
        TierLevel: '&eTier &cIII'
        Type: 'emerald'
        Time: 6
        Speed: 30
        Chat: '&aEmerald Generators &ehave been upgraded to Tier &4III'
      bed-break:
        TierName: 'Bed gone'
        Time: 7
      game-over:
        TierName: 'Game over'
        Time: 10
**Example Sword Tools Configuration**

    #Items that cannot be placed inside chests
    Anti-Chest:
      - WOOD_SWORD
      - WOODEN_SWORD
      - WOOD_PICKAXE
      - WOODEN_PICKAXE
      - IRON_PICKAXE
      - GOLD_PICKAXE
      - DIAMOND_PICKAXE
      - WOOD_AXE
      - WOODEN_AXE
      - STONE_AXE
      - IRON_AXE
      - DIAMOND_AXE
      - SHEARS
    
    #Items that cannot be dropped on the ground
    Anti-Drop:
      Enabled: true
      List:
        - WOOD_SWORD
        - WOODEN_SWORD
        - WOOD_PICKAXE
        - WOODEN_PICKAXE
        - IRON_PICKAXE
        - GOLD_PICKAXE
        - GOLDEN_PICKAXE
        - DIAMOND_PICKAXE
        - WOOD_AXE
        - WOODEN_AXE
        - STONE_AXE
        - IRON_AXE
        - GOLD_AXE
        - GOLDEN_AXE
        - DIAMOND_AXE
        - SHEARS
    
    
    Tools-Sold:
      Pickaxe-Types:
        - WOOD
        - IRON
        - GOLD
        - DIAMOND
      Axe-Types:
        - WOOD
        - STONE
        - IRON
        - DIAMOND
    
    #With this feature enabled players can only have one tool of a specific type at a time
    #Players will not be able to downgrade their tool if the is enabled
    #(Tool Mechanics like Hypixel)
    Advanced-Tool-Replacement:
      Enabled: true
      Force-Ordered: true
      Problem: You already have the same, or higher tier
      Force-Ordered-Problem: You need to have a previous tier first
    
    #Prevents players from buying multiple of the same swords, or lower tier swords
    Ordered-Sword-Buy:
      Enabled: false
      Problem: You already have the same, or higher tier
    
    #Advanced-Tool-Replacement must be true
    #When you die with a tool, you will get one tier lower when you respawn
    Degraded-Tool-BuyGroups: true
    
    #If you add your sword to a chest, and have no other sword
    #You will be given a wooden sword
    Always-Sword: true
    
    #Players will always have a sword if this is enabled
    #Gives you a Wooden Sword if no sword is detected
    Advanced-Sword-Drop:
      Enabled: true
      List:
        - STONE_SWORD
        - IRON_SWORD
        - DIAMOND_SWORD
    
    #Removes a Wooden-Sword if you buy a better sword
    #If 'all-type' is set to TRUE, ALL sword types will get replaced.
    #Otherwise, only wooden swords will get replaced (Like Hypixel)
    Replace-Sword-On-Buy:
      Enabled: true
      All-Type: false
    
    #Add Items here that you do not want to be effected by Advanced Swords and Tools
    #For example, A CUSTOM special item that is Golden Sword
    #ADD ITEMS BY THEIR DISPLAY NAME (DO NOT include color codes)
    Do-Not-Effect:
      - ''

