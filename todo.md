# To Do

## 0.0.1 Release
- [2022-07-11 done] Add Integrator Egg to dungeon loot table
- [2022-07-09 done] Add charnel compactor
- [2022-07-11 done] Add recipe for Charnel Compactor
- [2022-07-11 done] Add preferred tools for blocks
- [2022-07-11 done] Fix block hardness values
- [2022-07-11 done] Fix block tool settings
- [2022-07-11 done] Add recipes for item pipes
- [2022-07-11 done] Add walls to wall tag
- Add recipes for building blocks
- [done] Fix hopper extension recipe
- [done] Add break particles for item pipes
- [done] Add break particles for Charnel CompactorG
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
- Balance Transducer and Stirling Engine
- [done] Add minimum fuel influx for heater activation
- [done] Remove 'realistic fluids'
- [done] Add cutting recipes for building blocks
- Improve world loading speed by serialising fluid networks
- [done] Create REI plugin
- [done] Add recipes for Hydraulic Press, Transducer, Casting Basin, Trommel, Mixer
- [done] Add recipe for biomechanical components
- Add Meat Grinder
- [done] Add Enlightening recipe type
- Add REI support for Enlightening
- [done] Give Integrator a Data resource bufer
- [done] Add Blood Bubble Tree
- Remove stupid items (spigot, etc.)
- [done] Shave down the number of failed networks by storing whether a node has a network
- Move machine blocks and entities together
- Make SoundRegistry general
- Separate modules

## Later

- Add simple uses for Whisper Wheat (food item, etc.)
- Add coloured fluid pipes
- Add flywheel linkage
- Add BIG FAN
- Make small multi tank usable
- Sort out the Process Tank

- Improve speed of FluidNetwork node lookup
- Work out what the hell's going on with BlockApiCache in FluidInterfaceBlockEntity

## Much Later

- Add Feeding Trough
- [part] Add Caution Tape (looks similar to redstone)
- Pipe Tree? 
- Add MultiTank
- Think of an auto-crafting solution
- Item pipe routing
- PLC and bundled cables!

## Fixes
- Fix inconsistent ArrayStoreException after rebuilding fluid networks many times
- Fix ConcurrentModificationException when ticking world networks
- Secure blood machine fluid input detection by transferring irreversible things to a transaction callback

## Ore Processing

- [done] Add crucible and recipes
- [done] Add dynamic creation of Raw Ore Fat
- [done] Add Trommel
- [done] Add casting basin and hydraulic press

## MEAT Production

We need to differentiate ourselves from MineFactory and the other one.

- Add Biomass Reprocessor
- Add Meat fluid
- Add Enlightened Meat fluid
- Add Meat Brick
- Add Enlightened Meat Brick

- Add casts for Casting Basin?

## Integrator

- Add data requirement for Charnel Compactor
- Add Enlightening recipes for the Gland Potato and [nyi] Enlightened Meat

## Potion Effects

- Enlightenment

## Alloys
- [done] Add Meat Potato farming
- [done] Add Whisper Wheat processing
- [done] Create Whisper Brass ingot
- [done] Update item pipe recipes
- Add Meat Steel

## Other
- [part] Add Data cables
- Change recipe outputs depending on Integrator connection
  - Without: WW -> grind -> seeds, meat -> 
- Add grinder
- Add trommel

## REI Plugin
- Mixer recipes
- Heating recipes
- Charnel Compactor recipe

## Fixes

- Fix item pipe interaction with Charnel Compactor

# Insane Ramblings

Blood bubble trees!

All are made one in the maw of the Biomass Reprocesor.

An Integrator is a biomechanical intelligence construct with two functions: to to ease the production of machines and to process eldritch knowledge. Integrators are designed to withstand high quantities of inhuman knowledge.

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

## Key Crafting Types

Mixing
- Mixer and Process Tank

Forging
- Whisper Forge

Crafting
- Vanilla Crafting Table and Assembly Table


## Ore Duplication
- [nyi] Grind up some raw ore in a crusher
- [nyi] Remove the raw ore and dissolve in in ELDRITCH ENZYMES using a [nyi] Mixer or a [pi] Process Tank
- [nyi] Insert the ore fat into trommel to extract MORE ORES!
- [nyi] Insert MEAT into the trommel to extract DIVINE FLESH

## Ethereal Fuel Production

- [nyi] Heating coal or other fuels in a [nyi] rendering basin will create Ethereal Fuel in an attached [nyi] alembic.


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

- Pipes and redstone pumps are obtainable from Vanilla resources

- Find an Integrator Egg in a dungeon
- Offer a bucket of blood to the Intergrator Egg to hatch it
- Insert flesh into a Charnel Compactor to get Integration Charges

# XP System

Surrogate nervous system collects XP. Taking cuttings gives XP-rich nervous tissue. These can be ground up to make XP meat. This can be packed XP cans or diffused into the world, at a loss.

# Rocket-Plane

Rocket Plane is nuclar powered! Rocket Plane can transport you or your objects VAST distances! Rocket Plane is our best friend!

# Auto-Crafting Solutions

- Nine display platforms are placed in a 3x3 square. 

# Achievements

Angel's Egg
- Find an Integrator Egg in a dungeon

Blessings of The Infant God
- Offer blood to an Integrator to make it hatch

The Yeast of Thoughts and Mind
- Obtain Whispers From Beyond by grinding up Whisper Wheat

MOLOCH!
- Grind up some flesh into MEAT paste

Body Hammer
- 

A Different God
- Obtain Divine Flesh

Heart Machine
- Place a Fuel Transducer


# Bespoke Energy System vs Work Fluid

## Cosmic Potential

- No need for separate motors on fluid processing machines.
- Marginally better performance
- More varied cables

## Work Fluid

- *I'm not like the other mods*


# Mod Page Text

Welcome to the world of NeepMeat, a 

tldr: cosmic-horror-cyber-gore-punk cheerful spooky meat RedPower ripoff

Fluids obey gravity, negating the need for pumps in well-designed systems.

Flow calculations are performed in a separate thread

Since this is version 0.1, there will likely be many issues with varying levels of seriousness. I haven't encountered anything that has permanently damaged a world, but for the love of Azathoth, DO NOT USE THIS VERSION IN A WORLD YOU CARE ABOUT WITHOUT BACKING UP.
