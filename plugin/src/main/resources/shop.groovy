/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming DoorWars.
 *
 * Screaming DoorWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming DoorWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming DoorWars. If not, see <https://www.gnu.org/licenses/>.
 */

inventory {

    category ('DIAMOND_CHESTPLATE;1;Armor;Protect yourself with armor! Click here.') {


        item('LEATHER_HELMET for 1 of bronze') {
            property 'applycolorbyteam'

            stack.enchant 'PROTECTION_ENVIRONMENTAL'
        }

        item('LEATHER_LEGGINGS for 1 of bronze') {
            property 'applycolorbyteam'

            stack.enchant 'PROTECTION_ENVIRONMENTAL'
        }

        item('LEATHER_BOOTS for 1 of bronze') {
            property 'applycolorbyteam'

            stack.enchant 'PROTECTION_ENVIRONMENTAL'
        }

        item('CHAINMAIL_CHESTPLATE for 1 of iron') {
            stack.enchant 'PROTECTION_ENVIRONMENTAL'
        }

        item('CHAINMAIL_CHESTPLATE for 3 of iron') {
            stack.enchant 'PROTECTION_ENVIRONMENTAL', 2
        }

        item('CHAINMAIL_CHESTPLATE for 7 of iron') {
            stack.enchant 'PROTECTION_ENVIRONMENTAL', 3
        }

        item('IRON_HELMET for 10 of gold') {
            stack {
                name 'Helm'
                enchant 'PROTECTION_ENVIRONMENTAL', 2
            }
        }
    }


    category ('DIAMOND_SWORD;1;Swords;Click here to get sword for attacking your enemies') {
        column 'center'

        item('STICK for 8 of bronze') {
            stack.enchant 'KNOCKBACK'
        }

        item('GOLDEN_SWORD for 1 of iron') {
            stack.enchant 'DAMAGE_ALL'
        }

        item('GOLDEN_SWORD for 3 of iron') {
            stack.enchant 'DAMAGE_ALL', 2
        }

        item('GOLDEN_SWORD for 7 of iron') {
            stack.enchant 'DAMAGE_ALL', 3
        }

        item('GOLDEN_SWORD for 3 of gold') {
            stack.enchant ([
                    'DAMAGE_ALL': 2,
                    'KNOCKBACK': 1
            ])
        }

        item('GOLDEN_AXE for 15 of gold') {
            stack {
                name 'Axe of Infinity'
                enchant (['DAMAGE_ALL', 'DURABILITY', 'KNOCKBACK'])
            }
        }

    }

    category ('BOW;1;Bows;Prove yourself as a marksman with these wonderful bows') {
        column 'right'

        item('BOW for 3 of gold') {
            stack.enchant 'ARROW_INFINITE'
        }

        item('BOW for 7 of gold') {
            stack.enchant (['ARROW_INFINITE', 'ARROW_DAMAGE'])
        }

        item('BOW for 13 of gold') {
            stack.enchant ([
                    'ARROW_INFINITE': 1,
                    'ARROW_DAMAGE': 2
            ])
        }

        item('BOW for 14 of gold') {
            stack.enchant (['ARROW_INFINITE',  'ARROW_FIRE'])
        }

        item('BOW for 15 of gold') {
            stack.enchant (['ARROW_INFINITE',  'ARROW_FIRE', 'ARROW_KNOCKBACK'])
        }

        item('ARROW for 1 of gold')
    }

    category ('COOKED_PORKCHOP;1;Eat;Are you hungry? Get something to eat.') {
        row 3
        column 2

        item ('COOKED_PORKCHOP;2 for 4 of bronze')
        item ('GOLDEN_APPLE for 2 of iron')
        item ('BREAD;10 for 5 of gold')
    }

    /* THIS PART IS NOT DONE, NOW I'M WORKING ON POTION-TYPE INTEGRATION */

    category ('POTION;1;Drinks;Not enough? Use these potions and you\'ll feel better!') {
        stack.potion 'strength'
        row 3
        column 6

        item ('POTION for 3 of iron') {
            stack.potion 'healing'
        }

        item ('POTION for 5 of iron') {
            stack.potion 'strong_healing'
        }

        item ('POTION for 7 of iron') {
            stack.potion 'swiftness'
        }

        item ('POTION for 1 of gold') {
            stack.potion 'strength'
        }

        item ('POTION for 3 of gold') {
            stack.potion 'regeneration'
        }
    }

    category ('SANDSTONE;1;Blocks;Get to the others! Possible with blocks.') {
        linebreak 'before'

        item('CUT_SANDSTONE;2 for 1 of bronze')
        item('END_STONE;3 for 7 of bronze')
        item('IRON_BLOCK for 3 of iron')
        item('GLOWSTONE;4 for 15 of bronze')
        item('GLASS for 4 of bronze') {
            property 'applycolorbyteam'
        }
    }

    category ('TNT;1;Useful Stuff;Looking for useful stuff? Here are some!') {
        column 'center'

        item('CHEST for 1 of iron')
        item('ENDER_CHEST for 1 of gold')
        item('TNT;1;Autoigniteable TNT for 3 of iron') {
            property 'AutoIgniteableTnt', [
                    'explosion-time': 5,
                    'damage-placer': false
            ]
        }
        item('GUNPOWDER;1;Warp Powder for 7 of iron') {
            stack.lore ([
                    "When using this powder you'll get",
                    "teleported to you spawn point within 6 seconds",
                    "Warning: Any movement will stop the process"
            ])

            property 'WarpPowder', [
                    delay: 6
            ]
        }
        item('STRING;1;Trap for 3 of iron') {
            stack.lore ([
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
                                            '==': 'org.bukkit.potion.PotionEffect',
                                            'effect': 15,
                                            'amplifier': 2,
                                            'duration': 100,
                                            'ambient': true,
                                            'has-particles': true,
                                            'has-icon': true
                                    ]
                            ],
                            [
                                    effect: [
                                            '==': 'org.bukkit.potion.PotionEffect',
                                            'effect': 18,
                                            'amplifier': 2,
                                            'duration': 100,
                                            'ambient': true,
                                            'has-particles': true,
                                            'has-icon': true
                                    ]
                            ],
                            [
                                    effect: [
                                            '==': 'org.bukkit.potion.PotionEffect',
                                            'effect': 2,
                                            'amplifier': 2,
                                            'duration': 100,
                                            'ambient': true,
                                            'has-particles': true,
                                            'has-icon': true
                                    ]
                            ]
                    ]
            ]
        }
        item('IRON_BOOTS;1;Magnet-Shoes for 6 of iron') {
            stack.lore([
                    "Wear those shoes and have a 75%",
                    "chance of getting no knockback!"
            ])

            property 'MagnetShoes', [
                    probability: 75
            ]
        }
        item('COMPASS;1;Tracker for 5 of iron') {
            stack.lore([
                    "Wanna know where your closest target is?",
                    "Let's try this out!"
            ])

            property 'tracker'
        }
        item('BLAZE_ROD;1;"Rescue Platform" for 15 of iron') {
            stack.lore([
                    "Protect yourself from falling into",
                    "void with a Rescue Platform.",
                    "This is your last hope!"
            ])

            property 'rescueplatform', [
                    delay: 5
            ]
        }
        item('ENDER_EYE;1;Arrow Blocker for 5 of gold') {
            stack.lore([
                    "Block arrows that are coming",
                    "for you with black magic.",
                    "I mean, with this item."
            ])

            property 'arrowblocker', [
                    delay: 5
            ]
        }
        item('BRICKS;1;Protection Wall for 64 of bronze') {
            stack.lore([
                    "Instantly builds a wall that",
                    "can save your life!"
            ])

            property 'protectionwall'
        }
        item('GHAST_SPAWN_EGG;1;Golem for 24 of iron') {
            stack.lore([
                    "An iron golem that will protect",
                    "your team from the enemies."
            ])

            property 'Golem', [
                    speed: 0.25,
                    follow: 16.0
            ]
        }
        item('SHEEP_SPAWN_EGG;1;TNT Sheep for 10 of gold') {
            stack.lore([
                    "Use the TNT-Sheep! It will",
                    "walk towards your closest enemy",
                    "and explode within 8 seconds!"
            ])

            property 'TNTSheep'
        }
        item('FIRE_CHARGE;1;Fireball for 40 of iron') {
            stack.lore([
                    "Is it a bird? Is it a plane?",
                    "By the time you know",
                    "you are dead!"
            ])

            property 'ThrowableFireball'
        }

    }

    category ('DIAMOND_PICKAXE;1;Pickaxes;Destroy others blocks? Use these pickaxes.') {
        column 'right'

        item('IRON_PICKAXE for 4 of bronze') {
            stack.enchant 'LOOT_BONUS_BLOCKS'
        }

        item('IRON_PICKAXE for 2 of iron') {
            stack.enchant 'LOOT_BONUS_BLOCKS', 2
        }

        item('IRON_PICKAXE for 1 of gold') {
            stack.enchant 'LOOT_BONUS_BLOCKS', 3
        }
    }

}