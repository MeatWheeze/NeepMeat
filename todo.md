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
- Added the 'Machine Surgery' crafting system
- Added a mob cloning system
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
- Bottle of Pasteurised milk
- Coarse meat slurry to fine meat slurry
- Alchemical still
- [done] Suction meat grinder
- 3x3 crusher
- [done] Canned meat! (works like IC2 cans)
- Ground infuser thing
- Flow-limiting valve (need DFS path finding for fluid network)
- [done] Fluid rationing thing
- Stop Pedestal from starting recipes once one is in progress
- Add guide entry for heating
- [done] Add guide entry for super mincer
- Add REI support for fluid heating
- [done] Fix basin not accepting items
- Fix CowEntityRendererMixin
- [done] Fix remote players getting dumped when Mixer starts
- [done] Add Small Mincer
- [done] Add meat mixing and cartons
- Unify power system

### Changelog

- Added Multi Tank block
- Added more food items

## Later

- Make guns usable on laggy servers
- Furniture 
- Do not eject grinder xp
- Add recipes for MeatWeapons
- Add coloured fluid pipes
- Transforming tools. Wow!
- Add flywheel linkage
- Add BIG FAN
- Sort out the Process Tank
- Improve Big Lever texture
- Add temperature requirements for alloy smelting
- [done] Add recipes for Scaffolding
- Stock-keeper block

- Improve speed of FluidNetwork node lookup
- Work out what the hell's going on with BlockApiCache in FluidInterfaceBlockEntity
- Fix motor retaining reference to BE

## Much Later

- [done] Add Feeding Trough (accepts meat or vegetable feed)
- [done] Think of an auto-crafting solution
- Dyeable Meat Steel armour
- [part] Add Caution Tape (looks similar to redstone)
- Pipe Tree? 
- [done] Add MultiTank
- Item pipe routing
- Try to make Assembler use screen handlers
- PLC and bundled cables!

What about a VR helmet that, when used in conjunction with DHRUBGGS or high Enlightenment, allows you to SEE the horrors around you?

## Upgrades

Requires a mob surgery processor to be attached to the Surgery Machine

- Extra mouth: Automatically eats food from your inventory
- Extra leg joints: Walk up full blocks
- Pineal eye: Reveals hidden things, increases rate of Enlightenment.
- Subsidiary brain: A modified ward-brain that reduces the maddening effects of Enlightenment.
- General-purpose tentacle: Works as a toggleable item magnet.
- Blessing of Mr Skeltal: Permanent feather-falling
- Control port: Allows upgrades to be controlled using a special item.

## Meat Refining

- Reconstituted meat has a 0.1 saturation boost over coarse meat

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

Raw Meat Brick can be crafted from 9 Meat Scraps, obtained from crushing charnel substrate.

- Add casts for Casting Basin?

## Integrator

- [done] Add data requirement for Charnel Compactor
- [done] Add Enlightening recipes for the Gland Potato and [nyi] Enlightened Meat

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

### Upgrades


Morphogenetic Substrate is made by mixing Body Compound with Charged Work Fluid. It can be pressed into plates.

## Cosmic Pylon

Stimulates the pineal glands of nearby players and allows strange structures to manifest.
- Must be connected to a Surgery Table to achieve certain effects
-

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

Blood bubble trees!

All are made one in the maw of the Biomass Reprocesor.

An Integrator is a biomechanical information processing system designed to ease the production of machines. Integrators are designed to withstand high quantities of inhuman knowledge.

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

Grant us eyes!
- Create an Enlightened Brain


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

- Fluid pipes: An extensive fluid transfer system is provided, complete with tanks and valves. Fluids obey gravity, so fewer pumps are required.
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
Body compound is a common crafting item in NeepMeat. To produce it, connect a Charnel Compactor to a fully-grown Integrator with Data Cables. The Charnel Compactor fuunctions much like a Composter, except it requires flesh. We hope that you have a burgeoning meat industrial complex.

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
