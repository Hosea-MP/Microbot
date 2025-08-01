/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.mountaindaughter;

import net.runelite.client.plugins.microbot.questhelper.bank.banktab.BankSlotIcons;
import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.npc.NpcHintArrowRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Operation;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class MountainDaughter extends BasicQuestHelper
{
	//Items Required
	private ItemRequirement axe, pickaxe, whitePearl, whitePearlSeed, mud, plank, muddyRocks5, safetyGuarantee,
		halfRock, gloves, corpse, pole, rope, necklace;

	//Items Recommended
	private ItemRequirement slayerRing, combatGear;

	private Requirement onIsland1, onIsland2, onIsland3, inTheCamp, askedAboutDiplomacy, askedAboutFoodAndDiplomacy,
		spokenToSvidi, spokenToBrundt, minedRock,
		gottenGuarantee, givenGuaranteeToSvidi, finishedDiplomacy, finishedFood, finishedFoodAndDiplomacy, inKendalCave,
		fightableKendalNearby, hasBuried, rubbedMudIntoTree;

	private QuestStep enterCamp, enterCampOverRocks, talkToHamal, digUpMud, pickupPole, rubMudIntoTree, climbTree, poleVaultRocks, plankRocks, listenToSpirit,
		plankRocksReturn, talkToHamalAfterSpirit, talkToJokul, talkToSvidi, speakToBrundt, getRockFragment, returnToBrundt, returnToSvidi, getFruit,
		eatFruit, returnToSpirit, returnToHamalAboutFood, returnToHamalAboutDiplomacy, talkToKendal, killKendal, noPlankRocksReturn, enterCave,
		grabCorpse, bringCorpseToHamal, collectRocks, createCairn, buryCorpseOnIsland, speakRagnar;

	//Zones
	private Zone CAMP_ZONE_1, CAMP_ZONE_2, CAMP_ZONE_3, LAKE_ISLAND_1, LAKE_ISLAND_2, LAKE_ISLAND_3, KENDAL_CAVE;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		loadConditions();
		loadQuestSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep enteringTheCamp = new ConditionalStep(this, enterCamp);
		enteringTheCamp.addStep(inTheCamp, talkToHamal);

		steps.put(0, enteringTheCamp);

		ConditionalStep speakToSpirit = new ConditionalStep(this, enterCampOverRocks);
		speakToSpirit.addStep(onIsland3, listenToSpirit);
		speakToSpirit.addStep(onIsland2, plankRocks);
		speakToSpirit.addStep(onIsland1, poleVaultRocks);
		speakToSpirit.addStep(new Conditions(inTheCamp, rubbedMudIntoTree), climbTree);
		speakToSpirit.addStep(new Conditions(inTheCamp, mud, pole), rubMudIntoTree);
		speakToSpirit.addStep(new Conditions(inTheCamp, mud), pickupPole);
		speakToSpirit.addStep(inTheCamp, digUpMud);

		steps.put(10, speakToSpirit);

		ConditionalStep helpTheCamp = new ConditionalStep(this, enterCampOverRocks);
		helpTheCamp.addStep(finishedFoodAndDiplomacy, returnToSpirit);
		helpTheCamp.addStep(new Conditions(givenGuaranteeToSvidi, finishedFood), returnToHamalAboutDiplomacy);

		// Get fruit
		helpTheCamp.addStep(new Conditions(givenGuaranteeToSvidi, whitePearlSeed.alsoCheckBank(questBank)), returnToHamalAboutFood);
		helpTheCamp.addStep(new Conditions(givenGuaranteeToSvidi, whitePearl.alsoCheckBank(questBank)), eatFruit);
		helpTheCamp.addStep(givenGuaranteeToSvidi, getFruit);

		// Fremennik friendship
		helpTheCamp.addStep(gottenGuarantee, returnToSvidi);
		helpTheCamp.addStep(minedRock, returnToBrundt);
		helpTheCamp.addStep(spokenToBrundt, getRockFragment);
		helpTheCamp.addStep(spokenToSvidi, speakToBrundt);
		helpTheCamp.addStep(askedAboutFoodAndDiplomacy, talkToSvidi);
		helpTheCamp.addStep(askedAboutDiplomacy, talkToJokul);
		helpTheCamp.addStep(onIsland3, plankRocksReturn);
		helpTheCamp.addStep(inTheCamp, talkToHamalAfterSpirit);

		steps.put(20, helpTheCamp);

		ConditionalStep talkKendal = new ConditionalStep(this, enterCave);
		talkKendal.addStep(onIsland3, noPlankRocksReturn);
		talkKendal.addStep(inKendalCave, talkToKendal);

		steps.put(30, talkKendal);

		ConditionalStep killKendalStep = new ConditionalStep(this, enterCave);
		killKendalStep.addStep(fightableKendalNearby, killKendal);
		killKendalStep.addStep(inKendalCave, talkToKendal);

		steps.put(40, killKendalStep);

		ConditionalStep returnTheCorpse = new ConditionalStep(this, enterCave);
		returnTheCorpse.addStep(corpse.alsoCheckBank(questBank), bringCorpseToHamal);
		returnTheCorpse.addStep(inKendalCave, grabCorpse);

		steps.put(50, returnTheCorpse);

		ConditionalStep buryCorpse = new ConditionalStep(this, enterCampOverRocks);
		buryCorpse.addStep(hasBuried, createCairn);
		buryCorpse.addStep(necklace.alsoCheckBank(questBank), buryCorpseOnIsland);
		buryCorpse.addStep(muddyRocks5.alsoCheckBank(questBank), speakRagnar);
		buryCorpse.addStep(inTheCamp, collectRocks);

		steps.put(60, buryCorpse);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		CAMP_ZONE_1 = new Zone(new WorldPoint(2758, 3660, 0), new WorldPoint(2821, 3664, 0));
		CAMP_ZONE_2 = new Zone(new WorldPoint(2767, 3653, 0), new WorldPoint(2821, 3712, 0));
		CAMP_ZONE_3 = new Zone(new WorldPoint(2751, 3671, 0), new WorldPoint(2767, 3712, 0));

		LAKE_ISLAND_1 = new Zone(new WorldPoint(2770, 3681, 0), new WorldPoint(2775, 3688, 0));
		LAKE_ISLAND_2 = new Zone(new WorldPoint(2770, 3689, 0), new WorldPoint(2776, 3694, 0));
		LAKE_ISLAND_3 = new Zone(new WorldPoint(2776, 3688, 0), new WorldPoint(2787, 3698, 0));

		KENDAL_CAVE = new Zone(new WorldPoint(2828, 10118, 0), new WorldPoint(2746, 10047, 0));
	}

	@Override
	protected void setupRequirements()
	{
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		pickaxe = new ItemRequirement("Any pickaxe", ItemID.BRONZE_PICKAXE).isNotConsumed();
		pickaxe.addAlternates(ItemCollections.PICKAXES);

		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed();
		plank = new ItemRequirement("Any plank", ItemID.WOODPLANK).isNotConsumed();
		plank.addAlternates(ItemID.PLANK_OAK, ItemID.PLANK_TEAK, ItemID.PLANK_MAHOGANY);
		pole = new ItemRequirement("A staff or a pole", ItemID.MDAUGHTER_STICK).isNotConsumed();
		pole.addAlternates(ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF, ItemID.PLAINSTAFF, ItemID.BATTLESTAFF);
		pole.addAlternates(ItemCollections.AIR_STAFF);
		pole.addAlternates(ItemCollections.WATER_STAFF);
		pole.addAlternates(ItemCollections.EARTH_STAFF);
		pole.addAlternates(ItemCollections.FIRE_STAFF);
		pole.setTooltip("A Dramen Staff will NOT work. A pole can be obtained from the goat pen north of Hamal's tent.");
		gloves = new ItemRequirement("Almost any gloves", ItemID.LEATHER_GLOVES).isNotConsumed();
		gloves.addAlternates(ItemID.HUNDRED_GAUNTLETS_LEVEL_10, ItemID.HUNDRED_GAUNTLETS_LEVEL_9, ItemID.HUNDRED_GAUNTLETS_LEVEL_8, ItemID.HUNDRED_GAUNTLETS_LEVEL_7, ItemID.HUNDRED_GAUNTLETS_LEVEL_6,
			ItemID.HUNDRED_GAUNTLETS_LEVEL_5, ItemID.HUNDRED_GAUNTLETS_LEVEL_4, ItemID.HUNDRED_GAUNTLETS_LEVEL_3, ItemID.HUNDRED_GAUNTLETS_LEVEL_2, ItemID.HUNDRED_GAUNTLETS_LEVEL_1,
			ItemID.FEROCIOUS_GLOVES, ItemID.GRACEFUL_GLOVES, ItemID.GRANITE_GLOVES);
		gloves.setTooltip("You can use most other gloves, with a few exceptions (Slayer, Mystic, Ranger, Moonclan, Lunar, Infinity, vambraces).");

		mud = new ItemRequirement("Mud", ItemID.MDAUGHTER_MUD);
		mud.setTooltip("You can get some mud from the mud pool south of Hamal's tent.");

		halfRock = new ItemRequirement("Half a rock", ItemID.MDAUGHTER_HALF_ROCK);
		halfRock.setTooltip("You can get another piece by using a pickaxe on the Ancient Rock in the Mountain Camp.");

		safetyGuarantee = new ItemRequirement("Safety Guarantee", ItemID.MDAUGHTER_SAFETY_GUARANTEE);
		safetyGuarantee.setTooltip("You can get another guarantee from Brundt in Rellekka's longhall.");

		whitePearl = new ItemRequirement("White pearl", ItemID.MDAUGHTER_WHITE_PEARL_FRUIT);
		whitePearlSeed = new ItemRequirement("White pearl seed", ItemID.MDAUGHTER_WHITE_PEARL_SEED);
		corpse = new ItemRequirement("Corpse of woman", ItemID.MDAUGHTER_DAUGHTER_CORPSE);
		corpse.setTooltip("You can find this corpse again in the Kendal's cave.");
		muddyRocks5 = new ItemRequirement("Muddy rock", ItemID.MDAUGHTER_ROCK, 5);
		slayerRing = new ItemRequirement("Slayer ring for teleports", ItemCollections.SLAYER_RINGS);
		combatGear = new ItemRequirement("Combat gear for The Kendal fight", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		necklace = new ItemRequirement("Asleif's necklace", ItemID.MDAUGHTER_NECKLACE);
	}

	private void loadConditions()
	{
		onIsland1 = new Conditions(new ZoneRequirement(LAKE_ISLAND_1));
		onIsland2 = new Conditions(new ZoneRequirement(LAKE_ISLAND_2));
		onIsland3 = new Conditions(new ZoneRequirement(LAKE_ISLAND_3));

		inTheCamp = new Conditions(new ZoneRequirement(CAMP_ZONE_1, CAMP_ZONE_2, CAMP_ZONE_3));
		askedAboutDiplomacy = new Conditions(new VarbitRequirement(262, 10));
		rubbedMudIntoTree = new Conditions(new VarbitRequirement(261, 1));

		VarbitRequirement askedAboutFood = new VarbitRequirement(VarbitID.MDAUGHTER_FOOD_VAR, 10, Operation.GREATER_EQUAL);
		askedAboutFoodAndDiplomacy = new Conditions(new VarbitRequirement(262, 10), askedAboutFood);
		spokenToSvidi = new Conditions(new VarbitRequirement(262, 20), askedAboutFood);
		spokenToBrundt = new Conditions(new VarbitRequirement(262, 30), askedAboutFood);
		minedRock = new Conditions(new VarbitRequirement(262, 40), askedAboutFood);
		gottenGuarantee = new Conditions(new VarbitRequirement(262, 50), askedAboutFood);
		givenGuaranteeToSvidi = new Conditions(new VarbitRequirement(262, 60), askedAboutFood);
		finishedDiplomacy = new Conditions(new VarbitRequirement(266, 1));
		finishedFood = new VarbitRequirement(263, 20);
		finishedFoodAndDiplomacy = new Conditions(finishedDiplomacy, finishedFood);
		inKendalCave = new Conditions(new ZoneRequirement(KENDAL_CAVE));
		fightableKendalNearby = new Conditions(new NpcHintArrowRequirement(NpcID.MDAUGHTER_BEARMAN_FIGHTER));

		hasBuried = new Conditions(new VarbitRequirement(273, 1));
	}

	private void loadQuestSteps()
	{
		enterCamp = new ObjectStep(this, ObjectID.MDAUGHTER_CLIFF_BOULDER, new WorldPoint(2766, 3667, 0),
			"Use your rope on the boulder outside the Mountain Camp east of Rellekka.", rope.highlighted());
		enterCamp.addIcon(ItemID.ROPE);

		enterCampOverRocks = new ObjectStep(this, ObjectID.MDAUGHTER_ROCKSLIDE, new WorldPoint(2760, 3658, 0),
			"Return to the Mountain Camp.", rope.highlighted());

		talkToHamal = new NpcStep(this, NpcID.MDAUGHTER_HAMAL, new WorldPoint(2810, 3672, 0), "Speak to Hamal the Chieftain in the Mountain Camp.",
			rope, pickaxe, axe, plank, pole, gloves);
		talkToHamal.addDialogStep("Why is everyone so hostile?");
		talkToHamal.addDialogStep("So what are you doing up here?");
		talkToHamal.addDialogStep("I will search for her!");

		digUpMud = new ObjectStep(this, ObjectID.MDAUGHTER_ROOTS_1, new WorldPoint(2805, 3661, 0),
			"Head south of Hamal's tent and dig some mud from the mud pond.");
		pickupPole = new ItemStep(this, new WorldPoint(2813, 3685, 0), "Pick up the pole north of Hamal's tent.", pole);

		rubMudIntoTree = new ObjectStep(this, ObjectID.MDAUGHTER_LAKE_TREE, new WorldPoint(2772, 3681, 0),
			"Use mud on the Tall Tree on the lake north of the camp, and then climb it.",
			mud.highlighted(), pole, plank);
		rubMudIntoTree.addIcon(ItemID.MDAUGHTER_MUD);

		climbTree = new ObjectStep(this, ObjectID.MDAUGHTER_LAKE_TREE, new WorldPoint(2772, 3681, 0),
			"Climb the Tall Tree on the lake north of the camp.", pole, plank);

		poleVaultRocks = new ObjectStep(this, ObjectID.MDAUGHTER_POLEROCKS, new WorldPoint(2773, 3688, 0),
			"Use your pole or a staff on the clump of rocks.", pole, plank);
		poleVaultRocks.addIcon(ItemID.MDAUGHTER_STICK);

		plankRocks = new ObjectStep(this, ObjectID.MDAUGHTER_FLATSTONE1, new WorldPoint(2775, 3691, 0),
			"Use a plank on the flat stone.",
			plank);
		plankRocks.addIcon(ItemID.WOODPLANK);

		listenToSpirit = new ObjectStep(this, ObjectID.MDAUGHTER_SULPHAR_GAS, new WorldPoint(2781, 3689, 0),
			"Listen to the Shining Pool.");
		listenToSpirit.addDialogStep("Hello! Who are you?");
		listenToSpirit.addDialogStep("So what exactly do you want from me?");
		listenToSpirit.addDialogStep("That sounds like something I can do.");
		listenToSpirit.addDialogStep("I'll get right on it.");

		plankRocksReturn = new ObjectStep(this, ObjectID.MDAUGHTER_FLATSTONE2, new WorldPoint(2777, 3691, 0),
			"Use a plank on the flat stone to return to shore.",
			plank);
		plankRocksReturn.addDialogStep("Yes.");
		plankRocksReturn.addIcon(ItemID.WOODPLANK);

		talkToHamalAfterSpirit = new NpcStep(this, NpcID.MDAUGHTER_HAMAL, new WorldPoint(2810, 3672, 0),
			"Speak to Hamal the Chieftain in the Mountain Camp.",
			rope, pickaxe, axe, plank, pole, gloves);
		talkToHamalAfterSpirit.addDialogStep("About the people of Rellekka...");

		talkToJokul = new NpcStep(this, NpcID.MDAUGHTER_JOKUL, new WorldPoint(2812, 3680, 0),
			"Speak to Jokul north of Hamal's tent.");

		talkToSvidi = new NpcStep(this, NpcID.MDAUGHTER_SVIDI, new WorldPoint(2717, 3666, 0),
			"Speak to Svidi who roams in the forest east of Rellekka.",
			pickaxe);
		talkToSvidi.addDialogStep("Can't I persuade you to go in there somehow?");

		speakToBrundt = new NpcStep(this, NpcID.VIKING_BRUNDT_CHILD, new WorldPoint(2658, 3669, 0),
			"Speak to Brundt the Chieftain in the Rellekka's longhall.",
			rope, pickaxe, axe, plank, pole, gloves);
		speakToBrundt.addDialogStep("Ask about the mountain camp.");
		speakToBrundt.addDialogStep("Did it look pretty?");

		getRockFragment = new ObjectStep(this, ObjectID.MDAUGHTER_ANCIENT_ROCK, new WorldPoint(2799, 3660, 0),
			"Use a pickaxe on the Ancient Rock in the Mountain Camp.",
			pickaxe);
		getRockFragment.addIcon(ItemID.BRONZE_PICKAXE);

		returnToBrundt = new NpcStep(this, NpcID.VIKING_BRUNDT_CHILD, new WorldPoint(2658, 3669, 0),
			"Return to Brundt the Chieftain in the Rellekka's longhall.",
			halfRock);
		returnToBrundt.addDialogStep("Ask about the mountain camp.");

		returnToSvidi = new NpcStep(this, NpcID.MDAUGHTER_SVIDI, new WorldPoint(2717, 3666, 0),
			"Return to Svidi who roams in the forest east of Rellekka.",
			safetyGuarantee);

		getFruit = new ObjectStep(this, ObjectID.MDAUGHTER_WHITE_PEARL_BUSH, new WorldPoint(2849, 3497, 0),
			"Go to the top of White Wolf Mountain and pick the Thorny Bushes whilst wearing gloves.",
			gloves);

		eatFruit = new DetailedQuestStep(this, "Eat the White Pearl.", whitePearl.highlighted());

		returnToHamalAboutDiplomacy = new NpcStep(this, NpcID.MDAUGHTER_HAMAL, new WorldPoint(2810, 3672, 0),
			"Return to Hamal the Chieftain in the Mountain Camp.",
			whitePearlSeed);
		returnToHamalAboutDiplomacy.addDialogStep("About the people of Rellekka...");

		returnToHamalAboutFood = new NpcStep(this, NpcID.MDAUGHTER_HAMAL, new WorldPoint(2810, 3672, 0),
			"Return to Hamal the Chieftain in the Mountain Camp.",
			whitePearlSeed);
		returnToHamalAboutFood.addDialogStep("About your food supplies...");

		returnToHamalAboutDiplomacy.addSubSteps(returnToHamalAboutFood);

		returnToSpirit = new ObjectStep(this, ObjectID.MDAUGHTER_SULPHAR_GAS, new WorldPoint(2781, 3689, 0),
			"Return to the centre of the pool north of the Mountain Camp and listen to it.",
			pole, plank);

		noPlankRocksReturn = new ObjectStep(this, ObjectID.MDAUGHTER_FLATSTONE2, new WorldPoint(2777, 3691, 0),
			"Attempt to jump across the flat stone WITHOUT a plank to return to the north shore.");

		enterCave = new ObjectStep(this, ObjectID.MDAUGHTER_CAVEENTRANCE, new WorldPoint(2809, 3703, 0),
			"Cut through the trees north east of the lake and enter the cave there. Bring combat gear.",
			axe);
		((ObjectStep) enterCave).addTileMarker(new WorldPoint(2802, 3703, 0), SpriteID.COMBAT_STYLE_AXE_CHOP);
		((ObjectStep) enterCave).addTileMarker(new WorldPoint(2807, 3703, 0), SpriteID.COMBAT_STYLE_AXE_CHOP);

		talkToKendal = new NpcStep(this, NpcID.MDAUGHTER_BEARMAN, new WorldPoint(2788, 10081, 0),
			"Speak to the Kendal, then kill him.");
		talkToKendal.addDialogStep("It's just me, no one special.");
		talkToKendal.addDialogStep("You mean a sacrifice?");
		talkToKendal.addDialogStep("You look like a man in a bearsuit!");
		talkToKendal.addDialogStep("Can I see that corpse?");
		talkToKendal.addDialogStep("I humbly request to be given the remains.");
		talkToKendal.addDialogStep("I will kill you myself!");

		killKendal = new NpcStep(this, NpcID.MDAUGHTER_BEARMAN, new WorldPoint(2788, 10081, 0), "Kill the kendal.");
		talkToKendal.addSubSteps(killKendal);

		grabCorpse = new TileStep(this, new WorldPoint(2784, 10078, 0), "Pick up the Corpse of Woman.");
		bringCorpseToHamal = new NpcStep(this, NpcID.MDAUGHTER_HAMAL, new WorldPoint(2810, 3672, 0),
			"Bring the corpse to Hamal.", corpse);
		bringCorpseToHamal.addDialogStep("But he's not a god!");
		bringCorpseToHamal.addDialogStep("I will.");

		collectRocks = new DetailedQuestStep(this, "Collect 5 Muddy Rocks from around the camp.", muddyRocks5);

		speakRagnar = new NpcStep(this, NpcID.MDAUGHTER_RAGNAR, new WorldPoint(2766, 3676, 0),
			"Speak to Ragnar.", corpse, muddyRocks5);
		speakRagnar.addDialogStep("Thank you. I will make sure she's given a proper burial now.");

		buryCorpseOnIsland = new TileStep(this, new WorldPoint(2782, 3694, 0), "Return to the centre of the lake and bury the corpse.",
			corpse, pole, plank);

		createCairn = new ObjectStep(this, ObjectID.MDAUGHTER_BURIALMOUND, new WorldPoint(2783, 3694, 0),
			"Use the Muddy rocks on the Burial Mound at the centre of the Mountain Camp's lake.",
			muddyRocks5);
		createCairn.addIcon(ItemID.MDAUGHTER_ROCK);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(rope);
		reqs.add(pickaxe);
		reqs.add(axe);
		reqs.add(plank);
		reqs.add(gloves);
		reqs.add(pole);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(slayerRing, combatGear);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("The Kendal (level 70)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new SkillRequirement(Skill.AGILITY, 20, true));
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
			new ExperienceReward(Skill.ATTACK, 1000),
			new ExperienceReward(Skill.PRAYER, 2000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("A Bearhead", ItemID.MDAUGHTER_BEAR_HELMET, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Mountain Camp"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Speak to Hamal", Arrays.asList(enterCamp, talkToHamal), rope, plank, pickaxe));
		allSteps.add(new PanelDetails("Go to the centre of the lake", Arrays.asList(digUpMud, pickupPole, rubMudIntoTree, climbTree,
			poleVaultRocks, plankRocks, listenToSpirit)));
		allSteps.add(new PanelDetails("Find out how to help", Arrays.asList(talkToHamalAfterSpirit, talkToJokul)));
		allSteps.add(new PanelDetails("Making peace with Rellekka", Arrays.asList(talkToSvidi, speakToBrundt, getRockFragment, returnToBrundt, returnToSvidi)));
		allSteps.add(new PanelDetails("Find a new food source", Arrays.asList(getFruit, eatFruit), axe, gloves));
		allSteps.add(new PanelDetails("Prepare for a fight", Collections.singletonList(new DetailedQuestStep(this, "Prepare to fight The Kendal (level 70)")), pole, plank, axe, whitePearlSeed));
		allSteps.add(new PanelDetails("Tell Hamal about your success", Collections.singletonList(returnToHamalAboutDiplomacy)));
		allSteps.add(new PanelDetails("Tell Asleif about your success", Collections.singletonList(returnToSpirit)));
		allSteps.add(new PanelDetails("Find Asleif's corpse", Arrays.asList(enterCave, talkToKendal, grabCorpse)));
		allSteps.add(new PanelDetails("Bring Asleif's corpse to Hamal", Collections.singletonList(bringCorpseToHamal)));
		allSteps.add(new PanelDetails("Bury Asleif", Arrays.asList(collectRocks, speakRagnar, buryCorpseOnIsland, createCairn)));

		return allSteps;
	}
}
