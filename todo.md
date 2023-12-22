# To Do

## 0.1.0 Release
- [2022-07-11 done] Add Integrator Egg to dungeon loot table
- [2022-07-09 done] Add charnel compactor
- [2022-07-11 done] Add recipe for Charnel Compactor
- [2022-07-11 done] Add preferred tools for blocks
- [2022-07-11 done] Fix block hardness values
- [2022-07-11 done] Fix block tool settings
- [2022-07-11 done] Add recipes for item pipes
- [2022-07-11 done] Add walls to wall tag
- [done] Add recipes for building blocks
- [done] Fix hopper extension recipe
- [done] Add break particles for item pipes
- [done] Add break particles for Charnel Compactor
- [done] Add a method for obtaining brains
- [done] Add recipe for Sacrificial Dagger
- [done] Add Mixer
- [done] Add Mixer recipes for Work Fluid and Patina Treatment
- [done] Change colour of Patina Treatment to cyan
- [done] Fix Polished Metal recipe
- [done] Change Work Fluid texture
- [done] Replace Whisper Wheat textures
- [done] Add a source of Whisper Wheat seeds
- [done] Stirling engine (model can have a little flywheel)
- [done] Fix pumps!
- [done] Stop duplication glitch when items enter rotuers
- [done] Standardise router inventory background
- [done] Make all machines change speed depending on rate of fluid input
- [done] Make grinder produce particles
- [done] Make Stirling Engine properly interface with motorised blocks
- [done] Make Mixer require a motor
- [done] Make Deployer run with a motor
- [done] Fix pumps extracting from multiple tanks
- [done] Add Rendering Basin
- [done] Add Ethereal Alembic
- [done] Fix Fluid Buffer connections
- [done] Balance Transducer and Stirling Engine
- [done] Add minimum fuel influx for heater activation
- [done] Remove 'realistic fluids'
- [done] Add cutting recipes for building blocks
- [done] Improve world loading speed by serialising fluid networks
- [done] Create REI plugin
- [done] Add recipes for Hydraulic Press, Transducer, Casting Basin, Trommel, Mixer
- [done] Add recipe for biomechanical components
- [done] Add Enlightening recipe type
- [done] Add REI support for Enlightening
- [done] Give Integrator a Data resource bufer
- [done] Add Blood Bubble Tree
- [done] Remove stupid items (spigot, etc.)
- [done] Shave down the number of failed networks by storing whether a node has a network
- [done] Move machine blocks and entities together
- [done] Make SoundRegistry general
- [done] Add Maceration Blades (whirly blades of death)
- [done] Add MEAT fluid
- [done] Add Meat Steel recipe
- [done] Add Raw Meat Brick recipe
- [done] Make fluid pipes retain connections
- [done] Make Charnel Compactor accept c:raw_meat
- [done] Remove redstone control in Breaker
- [done] Make Router use filter slots or something
- [done] Make Assembler require power
- [done] Fix motors retaining BE reference
- [done] Merge GrinderBlockEntity::storageToWorld
- [done] Test on server
- [done] Add Compound Injector
- [done] Scaffolding recipes
- [done] Fix fabric.mod.json license and description
- [done] Make repository public
- [done] Remove log messages when placing pipes!

## v0.1.2-alpha

- [done] Add data-driven integrated documentation
- [done] Write articles for all content
- [done] REI plugin for hydraulic press
- [done] Decrease death blades meat yield

## v0.1.3-alpha

- [done] Handle hydraulic press breaking during a recipe
- [done] Add simple uses for Whisper Wheat (food item, etc.)
- [done] Recipe remainders for blood buckets
- [done] Add feeding trough
- [done] Make Death Blades damage entities only when in motion
- [done] Make tanks retain NBT when broken
- [done] Align assembler inventory
- [done] Add recipe for Big Lever
- [done] Add machine surgery
- [done] Add recipe for Synthesiser
- [done] Make tanks drop properly - Not sure why this was happening and how it fixed itself. May occur again in the future.
- [done] Add guide entry for machine surgery
- [done] Pinkdrink

### Changelog

- Added the Feeding Trough
- Added the 'Machine Surgery' crafting plant
- Added a mob cloning plant
- Added a few food items
- Milk can be obtained by squeezing cows with a hydraulic press
- Fixed Airtruck issues with newer Geckolib versions
- Miscellaneous fixes and addtions
- Added more useless WIP blocks

## v0.1.4-alpha

- [done] Make Bottler wait for fluids before ejecting
- [done] Make Mixer use RecipeInputs
- [done] Add CronenCake
- [done] Make small multi tank usable
- [done] Add Fire Jet block
- [done] Improve fluid routing with filter pipe
- [done] Fluid heating in multi-tank (for making cooked meat slurry and other things)
- [done] Carton of Pasteurised milk
- [done] Tissue Slurry to Meat
- [done] Suction meat grinder
- [done] Add Meat carton.
- [done] Fluid rationing thing
- [done] Stop Pedestal from starting recipes once one is in progress
- [done] Add guide entry for heating
- [done] Add guide entry for super mincer
- [done] Add REI support for fluid heating
- [done] Add REI support for the Trommel
- [done] Fix basin not accepting items
- [done] Fix remote players getting dumped when Mixer starts
- [done] Add Small Mincer
- [done] Add meat mixing and cartons
- [done] Unify power plant
- [done] Replace the Sacrificial Dagger with Sacrificial Scalpel
- [done] Update guide entry for Sacrificial Dagger
- [done] Add transforming tools
- [done] Add REI plugin for transforming tools

### Changelog

- 'Fixed' a crash associated with a Mixin by removing it entirely.
- Fixed filling behaviour of the Bottler.
- Added more food items such as the Cronencake. Ignore its screams.
- Added a pipe for filtering fluids.
- Added the Fire Jet
- Added a method of combining any two tools with the Surgery Machine.
- Added a new Mincer.
- Meat is now produced by refining Tissue Slurry with the Trommel. The Mincer and Death Blades now produce Tissue Slurry.
- Using an Item Mincer, food items can be converted into a liquid which retains the food's hunger and saturation values. Fluids with different values can be mixed together.
- Added REI support for more recipe types.
- Various bug fixes and other minor alterations.

### v0.1.5-alpha

- [done] More flexible/complex/uniform energy production mechanics. They must be as complicated/intuitive as in Factorization.
- [done] Add Item Requester
- [done] Add Storage Bus
- [done] Overhaul pipe plant for performance
- [done] Make guns usable on laggy servers
- [done] Flow-limiting valve
- [done] Add some furniture 
- [done] Make pumps update adjacent fluid networks
- [done] Make fluid pipes retain their contents across reloads
- [done] Fix loading FluidPumps when creating networks from NBT
- [done] Expose inventory of Content Detector to allow easier shulker box systems
- [done] Update Guide entry for fluid pipes
- [done] Add Fluid Exciter
- [done] Update Guide entries for fluid exciter
- [done] Fix Item Pump eating items when there is no valid output


### Changelog

An new item routing plant is in the works! If you think you're too cool for Applied Energistics, or just a masochist, you can use this! Item pipe networks can be made smart by connecting them to a Pipe Driver. This will allow requesters and storage buses to communicate and send items to you.

Fluid pipes have been completely overhauled due to performance concerns. Previously, fluid would be transferred directly from storage to storage. This has been changed in favour of a more traditional approach where fluid is temporarily stored in pipes. For efficiency, only pipes with three or more connections will be ticked. For example, a pipe that connects two fluid storages only will only have two ticking block entities regardless of length.

Guns now have a trigger plant that is separate from Minecraft's normal use/attack signals. Not being the current focus, they are still largely useless. I'll get round to integrating them one day.
- Fix flow limiter/energy values

### v0.1.6-alpha

- Implement meat additives
- [done] Fix CowEntityRendererMixin
- [done] Improve Yellow Scaffolding top texture
- [done] Improve Big Lever texture
- [done] Do not eject grinder xp
- [done] Fix Grinder's axuiliary storage filling up
- [done] Add Assault Drill
- [done] Add Solidity Observer
- [done] Improve usability of Deployer
- [done] Assault drill
- [done] Add a way to reload the Assault Drill
- [part] Mob surgery machine
- [done] Fix Item Mincer Guide page.
- [done] Improve Bottler model
- [done] Improve Small Trommel model.
- [done] Make Surgery Table display its connected blocks

### Changelog

The Surgery Table now spawns particles at all vaid structure blocks when right-clicked.

I added the Solidity Observer purely so that I could make a concrete machine. It might also be useful with tree farms. It's pretty redundant, but it's there. Don't worry about it.

I made an Assault Drill. It's a drill, but you can use it to bore large holes in other living organisms, killing them in the process. It can be enchanted. 

Yellow Scaffolding now no longer looks like soggy cardboard.

Cows can be squished with the Hydraulic Press again. This is a very important feature.

Added some more useless nonsense that may never be finished: The God Worm, the Keeper, the Mob Surgery Machine, the Assault Drill (I like it when the drill goes GRRRRRRRR).

### v0.2.0-alpha-1.19.2

- [done] Fix everything that broke due to the changes in SingleVariantStorage
- [done] Add vascular conduits.
    - Remove right-click action
- [done] Fix occasional failure to resolve blood networks
- [done] Regular automatic feeding by Placing a motor under a Feeding Trough
- [done] Rename all interfaces for consistency
- [done] Fix Persistent Crafting table screen behaviour.
- [done] Add a sustainable way of getting brains.
- [done] Fix the strange thing where fluid networks don't load in a single chunk that resolves spontaneously
- [done] Overhaul fluid pipes
    - [done] Remove debug particles
    - [done] Add gravity
- [done] Add a motor that stores and uses Ethereal Fuel from Transducers.
- [done] Convert Fluid Exciter into Heart Machine
- [done] Add recipes for advanced motor and things
- [done] Update Guide entries for 
    - [done] Motor 
    - [done] Advanced Motor 
    - [done] Fluid Exciter
    - [done] Energetic fluids
    - [done] Transducer
    - [done] Vascular conduits
- [done] CC Jar-in-jar

### Play Testing

- Rusty pipe sides not rendering - Fixed by Indium
- Crash when interacting with tanks - Fixed by Indium
- Vascular networks don't save
    - Fixed by removing blocke entity pos check
    - Added log error when null network is detected
- Fluid pipes stop working after reload (need replacing)
    - Issue may be due to CCME
    - Fixed by storing queued nodes and vertices in NBT

### Changelog

The power plant has been changed once again! This time I caved in and abandoned the fluid gimmick. I have added cables. Except they're not called cables: they're called Vascular Conduits!

I have overhauled fluid pipes for the fourth time! They are now decentralised! This allowed me to remove loads of jank and weirdness. Hopefully they'll be slightly more stable now.

### v0.2.1-alpha-1.19.2

- [done] Fix Advanced Motor recipe
- [done] Reduce Copper Coil yield

### v0.2.2-alpha-1.19.2

- [done] Reduce amount of Ethereal Fuel produced
- [done] Add Homogeniser
- [done] Reduce minimum power for Grinder and other machines
- [done] Indicate whether a machine is operating
- [done] Fix up Death Blades
- [done] Improve Grinder model and animation
- [done] Add advanced tanks
- [done] Make tanks fill the tank below
- [done] Update Fluid Interface texture and model
- [done] Add PLC
- [done] Add tooltips for PLC components
- [done] Finish PLC immediate mode
- [done] Add manufacturing recipes and workpiece mechanics
- [done] PLC Recipes
    - [done] Reimplement all surgery recipes with the PLC
    - [done] Reimplement transforming tools with the PLC
    - [done] Add REI support for PLC recipes
- [done] PLC Redstone interface
- [done] Disable player movement when using the PLC
- [done] Cache manufacture step instances
- [done] Remove lore text from some items
- [done] Fix missing texture log messages that have accumulated
- [done] Limit PLC selection range
- [done] Instruction insertion at cursor
- [done] Allow instruction cancelling
- [done] Guide entry for PLC
- [done] Add recipe for Redstone Interface
- [done] Fix PLC recipe syncing
- [done] Update Github readme

## Changelog

The energy density of Ethereal Fuel has been increased drastically, so it should be easier to store in large quantities.

I added a larger tank. Tanks also drain into the tanks directly below them.

There are some helpful tooltips to indicate why a machine isn't running.

I decided that machine surgery recipes were too boring and derivative, so I added a programmable logic controller! I thought long and hard on how to make a real PLC intuitive and accessible in Minecraft and... well, I'm sure its usable. It's slightly limited on the logic side currently, but it can perform sequences of actions repeatedly. Not only does it provide a new crafting mechanic, but it also provides the means by which to automate it.

To do list dump:

### v0.2.3-alpha-1.19.2

- [done] Remove accidental use of Architectury class in PLCHudRenderer

### v0.2.4-alpha-1.19.2

- [done] Add Advanced Integrator
    - [done] Block and entity
    - [done] Enlightening recipes
    - [done] Viable route for obtaining
- [done] Add recipe for Transforming Tool base
- [done] Make the Drill break blocks
- Fix BigBlock entites not loading data on client
- [done] Add recipe for Divine Chrysalis
- Add recipe for AR Goggles
- [done] Add Upgrade manager
- [part] Upgrades for the Drill
- Properly implement data networks

## Later

- Multiple PLC actuators
- Enlightenment increase when near the Pylon
- Enlightenment effects
- [done] Remove pipes persistent state
- Fix the Death Blades. They stop working randomly.
- Reduce texture atlas use
    - [done] Integrator egg 
    - [done] Rusty pipe atlas x2 (no idea why they're still here)
    - Compound injector
    - Ejector
    - Advanced ejector
    - Heater
    - Large trommel
    - Something else with pipe and basalt colours
- Guide lookup from inventory
- Convert PLC robot to entity with collision detection
- Make PLC use power
- Immediate variables in PLC instructions
- [done] Add some player upgrades
    - [done] Guide entry
- Indicate the output direction on the Redstone Pump
- Add Enlightenment requirement for surgery recipes
- Add comparator output or some sort of sensor for fluid tanks
- [done] Add Hounds of Tyndalos
- [done] Add the Bovine Horror
- Make player certain implants compatible with armour
- Add a shield upgrade for the Assault Drill
- Add an easier way of getting Animal Feed
- Make the Stirling Engine use hot blocks instead of burning fuel.
- Add Dumping Track
- Add a thing that sprays acid, dissolving blocks.
- Fix REI warning message in Guide
- Energy limiter for network (vascular source converter)
- Add item interface
- Make Simulink in Minecraft! What could possibly go wrong?
- Update to 1.2000 with the bloody snoifflers and the bloody cherry nonsense. WOOO NEW FEATURESSS WOW!!! LET's MAKE TEHE GAME RUN EVEN SLOWER ON NORMAL HARDWARE!!! YOU ARE POOR IF YOU CAN'T AFFORD AN NVIDIA-1337gtx!!! AUtO-CRAFtING? LET'S ADD THAT TO VANILLA! STOP ADDING BLOAT! STOP IMPOSING YOUR VISION ON ME! MINECRAFT SHOULD BE A BLANK SLATE FOR ME TO CREATE MY OWN EXPERIENCE IN UNLIKE EVERY OTHER VIDEO GAME! 
- New energy system (vascular conduits replace pipes, add new motor that burns ethereal fuel from a buffer)
- Fix uneven fluid distribution
- Duat dimension
- God Worm
- Use on-demand sound instance for the Assault Drill rather a permanent one.
- [done] Remove 'Packet' from network class names that do not extend Packet
- [done] Add visceral conduit
- Fix flow limiter/energy values
- Battle jack
- Make Assault Drill break blocks
- Chutney!
- Add Damascus Rose (Makes villagers forget you)
- Add coloured fluid pipes
- Add mechanised villagers! (convert villagers into machines)
- Add Enlighted Ergot
- Status effects on meat cartons.
- Alchemical still
- Ground infuser thing
- 3x3 crusher
- Add recipes for MeatWeapons
- Add flywheel linkage
- Add BIG FAN
- Sort out the Process Tank
- Add temperature requirements for alloy smelting

- Pylon
    - Spawns Hounds if unstable
    - Installing a Pineal Eye allows you to see the Hounds

- Mob surgery:
    - Entity input
    - Controller block

## Much Later

- [done] Add Feeding Trough (accepts meat or vegetable feed)
- [done] Think of an auto-crafting solution
- Dyeable Meat Steel armour
- [part] Add Caution Tape (looks similar to redstone)
- Pipe Tree? 
- [done] Add MultiTank
- Try to make Assembler use screen handlers
- PLC and bundled cables!

What about a VR helmet that, when used in conjunction with DHRUBGGS or high Enlightenment, allows you to SEE the horrors around you?

## Oh Noes

Vascular networks have three parameters: Flux, potential and frequency. These are respectively analogous to current, voltage and frequency in a three-phase AC power system. 

In real life, the speed of an induction motor is determined by a complex interplay of the supply voltage and frequency, the load torque and the drawn current. This is confusing and difficult to implement. So what should we do instead?

## Data Cables

Idea 1:

- Data is a resource just like items or fluids. It cannot be copied, only transferred.
- Represented to the user as a type with an amount.

Idea 2:

- Data is a set of unique objects. These can be copied but become useless once one copy has been consumed.
- Represented to the user as a random name.

Data could be stored directly in a network. Capacity is increased by connecting extra devices... no, because when the network is altered the data will perssist in an unintuitive way.

## Item Routing

Storage bus:

- Gives advanced routing capabilities to adjacent inventroes

Tube driver

- A centralised controller for a tube network.
- Accepts power

Requester

- Displays a GUI showing all routable items

## Advanced Integrator

We need to give Data an actual unit.
The basic Integrator has insufficient data storage to perform certain recipes. Creating a Boivne Horror gives the resources necessary to construct an advanced Integrator. This gates variable frequency drives and more advanced power generation.

Connecting a Pylon to an advanced Integrator increases its data reserves further and improves collection speed.

## Robotic Arms

We could add an action in the PLC that allows you to change the selected actuator. This affects the instruction set.

The default actuator could be the PLC's robot. Robotic arms could be extra actuators.

## Upgrade Manager

This attaches to an entity or item workstation and displays the currently installed augmentations. It connects to a PLC and executes instructions to apply and remove them. We will have to add an visibility predicate to InstructionProvider to hide these special instructions in the main menu.

## Power Generation

General cycle: water/air -> ethereal fuel -> charged work fluid

## Upgrades

Requires a mob surgery processor to be attached to the Surgery Machine

- Extra mouth: Automatically eats food from your inventory
- Extra leg joints: Walk up full blocks
- Pineal eye: Reveals hidden things, increases rate of Enlightenment.
- Subsidiary brain: A modified ward-brain that reduces the maddening effects of Enlightenment.
- General-purpose tentacle: Works as a toggleable item magnet.
- Blessing of Mr Skeltal: Permanent feather-falling
- Rocket feet: Move quickly in stop motion
- Control port: Allows upgrades to be controlled using a special item.

There should also be a thing that allows you to transform into a large, powerful thing.

## Meat Refining

- Reconstituted meat has a 0.1 saturation boost over coarse meat

## PLC

Recipes are realised through sequences of operations applied using a PLC.

Recipe steps with identical parameters are currently created repeatedly. This is an inefficient use of memory, so I must add a caching system. Not sure what the best way to do this would be.

### Recipes

One major problem with the current recipe system is putting a base object in an invalid state, rendering it unusable. This can happen when recipes are changed after a step is applied or if a valid but incorrect step is accidentally applied by the player. To rectify this, I plan on adding a remove instruction which deletes the latest manufacture step from an item. Entities are slightly more complicated since they cannot be readily picked up and have their tooltips examined. This will result in situations where the player is unable to complete a recipe on an entity without knowing why. 

Entity-based recipes will match based on the suffix, rather than the whole set of steps. A recipe with length N will check the last N steps applied to the entity. This approach has similar issues to variable-length prefix codes except there are unlimited codewords that can be chosen arbitrarily by the user. Undefined behaviour will result from recipes which overlapping suffixes.

## Auto-Crafting Ideas

- Project Table is a crafting table with a persistent inventory.
- The Assembler sits on top of a project table or any other inventory and attempts to fill out a user-defined recipe
- Assembler detects an inventory implementation below it and displays the available slots. The user sets a filter for the input slots and selects the output slots.
- When running, the assembler takes items from its internal buffer and places them into the filtered slots, one at a time. When the item output appears, it will eject it.
- RECIPE REMAINDER HANDLING?

- Add outpt selection
- Add storage implementation
- Add power consumption
- Add brain and moving parts to model

## Meat Steel Recipe Ideas

- Compress Iron Ingot and MEAT with Hydraulic Press
- Combine 
- Use Forge Hammer [nyi] to pound Impure Meat Steel

## Ore Processing

- [done] Add crucible and recipes
- [done] Add dynamic creation of Raw Ore Fat
- [done] Add Trommel
- [done] Add casting basin and hydraulic press

## MEAT Production

We need to differentiate ourselves from MineFactory and the other one.

Organisms can be enlarged to produce more meat by injecting them with growth hormones.

Raw Meat Brick can be crafted from 9 Meat Scraps, obtained from crushing charnel substrate.

- Add casts for Casting Basin?

Meat can be further refined using a centrifuge. This produces fat and Reconstituted Meat.

Processing Meat in an alchemical still produces Visceral Essence.

## The Pylon

The Pylon is the true gateway to the outside, the regions beyond dreams.
When at operating speed, the Pylon's emenations stimulate the pineal glands of nearby vertebrates. This permits limited glimpses of the unseen. For unknown reasons, this process can also grant unseen forms substance in the waking world. 

In most organisms, these effects are unpredictable and thus cannot be harnessed consistently. Integrators, however, have the capacity to process and formalise the information and provide a stable interface for other machinery.

Construction:

The Pylon's efficacy is improved by mounting it on a pyramid of Meat Steel, similar to a beacon. Unlike a beacon, the efficacy of the Pylon only depends on the number of blocks in the pyramid; the structure does not have to be completely intact in order to be valid. This allows space for cables and pipes to feed the motor.

Instability is increased with higher efficacy. Supplying meat to the Pylon via two troughs eliminates this.

When the Pylon is too unstable, the Hounds of Tyndalos may be summoned.

The Instability Detector flashes red when a pylon is marginally unstable and sounds an alarm when highly unstable or if a Hound has materialised.

- Flashes red when unstable

## Reality Transition

I wish to stay away from Vanilla-style portals due to the implicit ethos of distain for Vanilla that I have so far failed to communicate.

This process is in some way facilitated by a Pylon. We could have the player build a complex structure in order to transport themselves to the Duat, but it does not make sense to not require such extensive infrastructure for the return journey.

We could have the player build a skin-ship, a vessel capable of transcending realities that must be surrounded by living nervous tissue to overcome the 'great pain' (stole this concept from Scanners Live In vain by Cordwainer Smith. The entire structure could be self-powered and piloted to different worlds. Transportation would be instantaneous. This would cause problems for blocks that store references to specific world positions. Thus, fluid networks, item networks, multi-blocks etc. would be broken on each journey.

What about using Valkyrien Skies?

## Ritual Procedures

Certain procedures have been seen to elicit responses from outside. Using information systems, ritual algorithms have been developed that are optimised for certain responses.

### Heart Extraction

The procedure is to be performed on a pyramid of Meat Steel.

- Move the objective organism into position.
- Apply the Severance Operator
- Extract the heart

## Large Organisms

### Crafting

Start with a regular mob in the centre of a surgery table. Add bits to it and the model grows at various stages of completion.

## Integrator

- [done] Add data requirement for Charnel Compactor
- [done] Add Enlightening recipes for the Gland Potato and [nyi] Enlightened Meat

When exposed to a Pylon and the correct hormonal catalyst, it will undergo transmigration.

## Item Routing

- Will only be possible with an attached tube driver (?)
- Each ItemInPipe will carry an array of 6-bit integers that encodes its desired route.
- When there is a mismatch between the item's route and the current pipe's connections, the route will be voided.
- If there are no branches, an ejector can check if the item can be inserted into the target inventory

## Potion Effects

- Enlightenment
- Bloodlust

## Mob Cloning

Use a burny machine to burn a mob to get its Essential Saltes.

## Surgery

Used to create more advanced machines and weapons
- Airtruck
- Upgradeable Meat Steel tools
- Better guns

Certain recipes require the presence of extra blocks

Morphogenetic Substrate is made by mixing Body Compound with Charged Work Fluid. It can be pressed into plates.

Crushing mobs gives tissue slurry. This can be centrifuged to produce Meat and Biomass. 

## Alloys

- [done] Add Meat Potato farming
- [done] Add Whisper Wheat processing
- [done] Create Whisper Brass ingot
- [done] Update item pipe recipes
- Add Meat Steel

## Other

- [done] Add Data cables
- [done] Change recipe outputs depending on Integrator connection
- [done] Add grinder
- [done] Add trommel

# Insane Ramblings

NEEPMeat asks the important questions in life: Is that rust or is that blood? And does it really matter?

Blood bubble trees!

All are made one in the maw of the Biomass Reprocesor.

An Integrator is a biomechanical information processing plant designed to ease the production of machines. Integrators are designed to withstand high quantities of inhuman knowledge.

The Integrator exudes alien microfauna that convert all biotic material into Body Compound, the basis of all machines. 

Ordinary wheat, spliced with members of the unseen biosphere. The rustling of its leaves whispers of the Divine.

Whisper Wheat can be ground into Whispers From Beyond. These can be processed by an Integrator to get Data.

Dissolving Whisper Flour in water gives Cosmic Fluid.

Dissolving Whisper Flour in blood gives Work Fluid.

An assembly table is used to construct late-game machinery.

The Enlightened enzymes of the Gland Potato concentrate the fleshly nature of certain minerals into a layer of ethereal fat. These fats can be rendered into tallows and smelted to form ingots. Further yields can be obtained by congealing the fat under Enlightened Water in a Trommel. Since Enlightneded Water reveals that which is unseen, extra refined material, and sometimes other substances, will manifest during the process.

Elution of a pure mineral fat with Enlightened Water will reveal that which is hidden, manifesting mineral crystals in a worldly phase. 

Divinity inspires growth, and this growth can be shepherded to generate resources. Innoculation of an animal with a mixture of Divine Flesh, integration fluid and a fleshly mineral will cause it to develop new tissues that bear the mineral in a latent form. The organism will soon lose mobility, so it must be fed by pumping blood into it. Once maturation is reached, macerated flesh can be rendered to produce Raw Ore fat. 

Something has changed it to an Enlightened form. It exudes the heady scent of the Divine.

Granting Enlightenment to a Warped Fungus gives it a meaty quality. Instead of fruit, the tree bears fleshy sacs filled with animal blood.

Diverse fleshes come together to form life anew within the biomass reprocessor.

There is no deep cosmic truth or oneness. This path - Enlightenment - is nothing but a path to madness.

## Key Crafting Types

Mixing
- Mixer and Process Tank

Crafting
- Vanilla Crafting Table and Surgery Table

Surgery Table


## Ore Duplication

- [nyi] Grind up some raw ore in a crusher
- [nyi] Remove the raw ore and dissolve in in ELDRITCH ENZYMES using a [nyi] Mixer or a [pi] Process Tank
- [nyi] Insert the ore fat into trommel to extract MORE ORES!
- [nyi] Insert MEAT into the trommel to extract DIVINE FLESH


## Sacrificial Dagger

- Sound event when charging attack
- Sound event for failed and successful attacks
- Particles for resulting heart item

## Casting

- The Hydraulic Press requires a constant source of pressure. When extending, it takes in fluid. When retracting, this fluid is sent to the exhaust.
- When above a casting basin, the press will only extend when a valid recipe is present. 

## Late-Game Machinery

Biomass reprocessor
- Better version of the grinder

# Early-Game Progress Path

Tier 1:
- Whisper Brass

Tier 2:
- Liquid Meat
- Meat Steel

Tier 3:
- Divine Alloy


- Pipes and redstone pumps are obtainable from Vanilla resources

- Find an Integrator Egg in a dungeon
- Offer a bucket of blood to the Intergrator Egg to hatch it
- Insert flesh into a Charnel Compactor to get Integration Charges

# XP System

Surrogate nervous plant collects XP. Taking cuttings gives XP-rich nervous tissue. These can be ground up to make XP meat. This can be packed XP cans or diffused into the world, at a loss.

# Rocket-Plane

Rocket Plane is nuclar powered! Rocket Plane can transport you or your objects VAST distances! Rocket Plane is our best friend!

# Auto-Crafting Solutions

- Nine display platforms are placed in a 3x3 square. 

# Bosses

## God Worm

And below all, I saw It. The old corpse, and the worm that devours It at Its twilit sepulchre. Let us usurp the worm. The God Worm.

Five actions:

- Bite: The worm retracts into the hole and quickly comes out, hitting the target vertially with the front of its head.
- Slam: The worm becomes completely vertical before slamming half of its body down on the target, applying damage radiating out from the centre in a line.
- Half Swing: The worm swings its body horizontally at head height in a 180° arc.
- Full Swing: The worm swings its body horizontally at head height in a 360° arc.
- Summon: 
- Roar: No damage is applied. The worm rears its maw and roars at the sky.

## All Your Base Are Belong To Us

Alirgth, how about this. We have shoot-em-up thing. We have a dimension filled with void and weirdness and there's this big blobby fleshy thing that shoots stuff at you and you need to be in some sort of flying vehicle/body to dodge them and pile projectiles into the floaty thing or its subsidiaries.

# Achievements

Angel's Egg
- Find an Integrator Egg in a dungeon

Blessings of The Infant God
- Offer blood to an Integrator to make it hatch

The Yeast of Thoughts and Mind
- Obtain Whispers From Beyond by grinding up Whisper Wheat

I did not hit her! I did naat!
- Use Damascus Rose to make a villager forget you

Type 1 Critical Effect Weapon

MOLOCH!
- Grind up some flesh into MEAT paste

Body Hammer
- 

A Different God
- Obtain Divine Flesh

Heart Machine
- Place a Fuel Transducer

Grant us eyes!
- Create an Enlightened Brain

A Soup-Like Homogenate

# Bespoke Energy System vs Work Fluid

## Cosmic Potential

- No need for separate motors on fluid processing machines.
- Marginally better performance
- More varied cables

## Work Fluid

- *I'm not like the other mods*


# Mod Page Text

All meat is an unknowing ritual sacrifice to the gods of nature. Through the sifting of flesh, we will find remnants of the Divine, and through them will be delivered enlightenment. At NEEP, we are pleased to announce a new range of superior, flesh-based industrial machines that will allow your company to augur the Divine with efficiency and excellence.

- Katherine Ursula Hydroxonium McSpree, Director of NEEP, 1922

Welcome to the world of NeepMeat, a Minecraft mod concerning automation, logistics and cosmic horror (WIP). 


## Feature Overview

- Fluid pipes: An extensive fluid transfer plant is provided, complete with tanks and valves. Fluids obey gravity, so fewer pumps are required.
- Item pipes: I miss RedPower 2.
- Block breakers and placers: 
- Advanced ore processing: Dissolve raw ores in Eldritch Enzymes, filter out impurities in a Trommel and produce ingots with the Casting Basin and Hydraulic Press.
- Building blocks: Plenty of rusty, dungy and... bloody? textures are provided. Perfect for decorating hospitals, abattoirs, cathedrals and more!
- Meat [zalgo pls]


## Feature Explanation

## Dependencies

- Geckolib ??? (Hard dependency)
- REI ??? (Optional but highly recommended for recipe )

tldr: cosmic-horror-cyber-gore-punk cheerful spooky meat RedPower ripoff

## Getting Started

- Obtaining an Integrator:
Integrator Eggs can be found in dungeon loot. To hatch one, you must obtain blood by slaughtering a mob over a Drain with the Sacrificial Dagger. This blood can then be pumped into the egg using fluid pipes. A fully-grown Integrator has an internal buffer of 'Enlightenment' that is used by workstations that are connected by Data Cables. This buffer fills up slowly over time and can be boosted by inserting Whisper Wheat.

- Obtaining Body Compound
Body compound is a common crafting item in NeepMeat. To produce it, connect a Charnel Compactor to a fully-grown Integrator with Data Cables. The Charnel Compactor fuunctions much like a Composter, except it requires flesh.

- Energy Transfer
Machines require energy which is transferred through various fluids.

- Crops
NeepMeat provides two major crops. Gland Potatoes can be processed into Eldritch Enzymes in a mixer. Whisper Wheat can be ground into Whisper Flour which can be fed to an Integrator to increase its Enlightenment.

- Enlightening
This is a recipe type where items are transmuted to special forms. To enlighten an item, connect a Pedestal that containing the item to a mature Integrator via Data Cables. Converting an item takes quantity of Enlightenment from the Integrator.

- Meat Steel
Meat steel is required for mid-tier machinery. It is made by combining an Iron Ingot and a Raw Meat Brick in an alloy furnace. 

## Potential Future Features

- Automatic farming
- Multiblock tank
- In-game guide tablet
- Advanced item and fluid logistics
- Programmable automation controller
- More eldritchness


Since this is version 0.1, there will likely be many issues with varying levels of seriousness. I haven't encountered anything that has permanently damaged a world, but for the love of Azathoth, DO NOT USE THIS VERSION IN A WORLD YOU CARE ABOUT WITHOUT BACKING UP.
