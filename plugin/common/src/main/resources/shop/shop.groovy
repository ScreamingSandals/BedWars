/*
 * Copyright (C) 2024 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

inventory {

    category ('diamond_chestplate;1;Armor;Protect yourself with armor! Click here.') {


        item('leather_helmet for 1 of bronze') {
            property 'applycolorbyteam'

            stack.enchant 'protection'
        }

        item('leather_leggings for 1 of bronze') {
            property 'applycolorbyteam'

            stack.enchant 'protection'
        }

        item('leather_boots for 1 of bronze') {
            property 'applycolorbyteam'

            stack.enchant 'protection'
        }

        item('chainmail_chestplate for 1 of iron') {
            stack.enchant 'protection'
        }

        item('chainmail_chestplate for 3 of iron') {
            stack.enchant 'protection', 2
        }

        item('chainmail_chestplate for 7 of iron') {
            stack.enchant 'protection', 3
        }

        item('iron_helmet for 10 of gold') {
            stack {
                name 'Helm'
                enchant 'protection', 2
            }
        }
    }


    category ('diamond_sword;1;Swords;Click here to get sword for attacking your enemies') {
        column 'center'

        item('stick for 8 of bronze') {
            stack.enchant 'knockback'
        }

        item('wooden_sword for 1 of iron') {
            stack.enchant 'sharpness'
        }

        item('stone_sword for 3 of iron') {
            stack.enchant 'sharpness'
        }

        item('iron_sword for 7 of iron') {
            stack.enchant 'sharpness'
        }

        item('iron_sword for 3 of gold') {
            stack.enchant ([
                    'sharpness': 1,
                    'knockback': 1
            ])
        }

    }

    category ('bow;1;Bows;Prove yourself as a marksman with these wonderful bows') {
        column 'right'

        item('bow for 3 of gold') {
            stack.enchant 'infinity'
        }

        item('bow for 7 of gold') {
            stack.enchant (['infinity', 'power'])
        }

        item('bow for 13 of gold') {
            stack.enchant ([
                    'infinity': 1,
                    'power': 2
            ])
        }

        item('bow for 16 of gold') {
            stack.enchant (['infinity',  'flame'])
        }

        item('bow for 18 of gold') {
            stack.enchant (['infinity',  'flame', 'punch'])
        }

        item('arrow for 1 of gold')
    }

    category ('cooked_porkchop;1;Eat;Are you hungry? Get something to eat.') {
        row 3
        column 2

        item ('cooked_porkchop;2 for 4 of bronze')
        item ('golden_apple for 16 of iron')
    }

    /* THIS PART IS NOT DONE, NOW I'M WORKING ON POTION-TYPE INTEGRATION */

    category ('potion;1;Drinks;Not enough? Use these potions and you\'ll feel better!') {
        stack.potion 'strength'
        row 3
        column 6

        item ('potion for 3 of iron') {
            stack.potion 'healing'
        }

        item ('potion for 5 of iron') {
            stack.potion 'strong_healing'
        }

        item ('potion for 1 of gold') {
            stack.potion 'swiftness'
        }

        item ('potion for 5 of gold') {
            stack.potion 'strength'
        }
    }

    category ('sandstone;1;Blocks;Get to the others! Possible with blocks.') {
        linebreak 'before'

        item('cut_sandstone;2 for 1 of bronze')
        item('end_stone;3 for 7 of bronze')
        item('iron_block for 3 of iron')
        item('glowstone;4 for 15 of bronze')
        item('glass for 4 of bronze') {
            property 'applycolorbyteam'
        }
        item('ladder for 2 of bronze')
    }

    category ('tnt;1;Useful Stuff;Looking for useful stuff? Here are some!') {
        column 'center'

        item('chest for 1 of iron')
        item('ender_chest for 1 of gold')
        item('tnt;1;Autoigniteable TNT for 3 of iron') {
            property 'AutoIgniteableTnt', [
                    'explosion-time': 5,
                    'damage-placer': false
            ]
        }
        item('gunpowder;1;Warp Powder for 7 of iron') {
            stack.lore([
                    "When using this powder you'll get",
                    "teleported to you spawn point within 6 seconds",
                    "Warning: Any movement will stop the process"
            ])

            property 'WarpPowder', [
                    delay: 6
            ]
        }
        item('string;1;Trap for 3 of iron') {
            stack.lore([
                    "Get informed if an enemy steps on your trap",
                    "and your enemy won't be able to move properly."
            ]);

            property 'Trap', [
                    data: [
                            [
                                    sound: 'ENTITY_SHEEP_AMBIENT'
                            ],
                            [
                                    effect: [
                                            'effect'   : 'blindness',
                                            'amplifier': 2,
                                            'duration' : 100,
                                            'ambient'  : true,
                                            'particles': true,
                                            'icon'     : true
                                    ]
                            ],
                            [
                                    effect: [
                                            'effect'   : 'weakness',
                                            'amplifier': 2,
                                            'duration' : 100,
                                            'ambient'  : true,
                                            'particles': true,
                                            'icon'     : true
                                    ]
                            ],
                            [
                                    effect: [
                                            'effect'   : 'slowness',
                                            'amplifier': 2,
                                            'duration' : 100,
                                            'ambient'  : true,
                                            'particles': true,
                                            'icon'     : true
                                    ]
                            ]
                    ]
            ]
        }
        item('iron_boots;1;Magnet-Shoes for 6 of iron') {
            stack.lore([
                    "Wear those shoes and have a 75%",
                    "chance of getting no knockback!"
            ])

            property 'MagnetShoes', [
                    probability: 75
            ]
        }
        item('compass;1;Tracker for 5 of iron') {
            stack.lore([
                    "Wanna know where your closest target is?",
                    "Let's try this out!"
            ])

            property 'tracker'
        }
        item('blaze_rod;1;"Rescue Platform" for 15 of iron') {
            stack.lore([
                    "Protect yourself from falling into",
                    "void with a Rescue Platform.",
                    "This is your last hope!"
            ])

            property 'rescueplatform', [
                    delay: 5
            ]
        }
        item('ender_eye;1;Arrow Blocker for 5 of gold') {
            stack.lore([
                    "Block arrows that are coming",
                    "for you with black magic.",
                    "I mean, with this item."
            ])

            property 'arrowblocker', [
                    delay: 5
            ]
        }
        item('bricks;1;Protection Wall for 64 of bronze') {
            stack.lore([
                    "Instantly builds a wall that",
                    "can save your life!"
            ])

            property 'protectionwall'
        }
        item('ghast_spawn_egg;1;Golem for 24 of iron') {
            stack.lore([
                    "An iron golem that will protect",
                    "your team from the enemies."
            ])

            property 'Golem', [
                    speed: 0.25,
                    'follow-range': 16.0
            ]
        }
        item('sheep_spawn_egg;1;TNT Sheep for 10 of gold') {
            stack.lore([
                    "Use the TNT-Sheep! It will",
                    "walk towards your closest enemy",
                    "and explode within 8 seconds!"
            ])

            property 'TNTSheep'
        }
        item('fire_charge;1;Fireball for 40 of iron') {
            stack.lore([
                    "Is it a bird? Is it a plane?",
                    "By the time you know",
                    "you are dead!"
            ])

            property 'ThrowableFireball'
        }

        item('egg;1;Bridge Egg for 20 of gold') {
            stack.lore([
                    "This egg creates a trail of bridge after it's thrown"
            ])

            property 'BridgeEgg'
        }
    }

    category ('diamond_pickaxe;1;Pickaxes;Destroy others blocks? Use these pickaxes.') {
        column 'right'

        item('wooden_pickaxe for 4 of bronze') {
            stack.enchant 'efficiency'
        }

        item('stone_pickaxe for 2 of iron') {
            stack.enchant 'efficiency'
        }

        item('iron_pickaxe for 1 of gold') {
            stack.enchant 'efficiency'
        }
    }

}
