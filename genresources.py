#! /usr/bin/env python3

import mcresources
rm = mcresources.ResourceManager('neepmeat')

blocks = [
'grey_rough_concrete',
'yellow_rough_concrete',
'white_rough_concrete',
'yellow_tiles',
'filled_scaffold',
'caution_block',
'polished_metal_small_bricks',
'dirty_red_tiles',
'dirty_white_tiles',
'sandy_bricks',
'blood_bubble_planks',
'meat_steel_block',
'reinforced_glass'
]

for name in blocks:
    bl = rm.block(name)
    bl.make_slab()
    bl.make_stairs()
    bl.make_wall()

#scaffold_stairs = rm.block('rusted_scaffold')
#scaffold_stairs.make_stairs()
#scaffold_stairs.make_slab()
