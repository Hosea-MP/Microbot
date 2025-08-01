/*
 * Copyright (c) 2020, Zoinkwiz
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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.lostcity;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.NpcCondition;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemOnTileRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class LostCity extends BasicQuestHelper
{
	//Items Required
	ItemRequirement knife, axe, combatGear, teleport, bronzeAxe, dramenBranch, dramenStaff, dramenStaffEquipped;

	Requirement onEntrana, inDungeon, shamusNearby, bronzeAxeNearby, dramenSpiritNearby;

	DetailedQuestStep talkToWarrior, chopTree, talkToShamus, goToEntrana, goDownHole, getAxe, pickupAxe, attemptToCutDramen, killDramenSpirit, cutDramenBranch,
		teleportAway, craftBranch, enterZanaris, getAnotherBranch;

	//Zones
	Zone entrana, entranaDungeon;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToWarrior);

		ConditionalStep findShamus = new ConditionalStep(this, chopTree);
		findShamus.addStep(shamusNearby, talkToShamus);

		steps.put(1, findShamus);

		ConditionalStep killingTheSpirit = new ConditionalStep(this, goToEntrana);
		killingTheSpirit.addStep(new Conditions(inDungeon, dramenSpiritNearby), killDramenSpirit);
		killingTheSpirit.addStep(new Conditions(inDungeon, bronzeAxe), attemptToCutDramen);
		killingTheSpirit.addStep(new Conditions(inDungeon, bronzeAxeNearby), pickupAxe);
		killingTheSpirit.addStep(inDungeon, getAxe);
		killingTheSpirit.addStep(onEntrana, goDownHole);

		steps.put(2, killingTheSpirit);

		ConditionalStep finishQuest = new ConditionalStep(this, getAnotherBranch);
		finishQuest.addStep(new Conditions(inDungeon, dramenStaff), teleportAway);
		finishQuest.addStep(dramenStaff, enterZanaris);
		finishQuest.addStep(new Conditions(inDungeon, dramenBranch), teleportAway);
		finishQuest.addStep(dramenBranch, craftBranch);
		finishQuest.addStep(new Conditions(inDungeon, bronzeAxe), cutDramenBranch);
		finishQuest.addStep(new Conditions(inDungeon, bronzeAxeNearby), pickupAxe);
		finishQuest.addStep(inDungeon, getAxe);
		finishQuest.addStep(onEntrana, goDownHole);

		steps.put(3, finishQuest);

		steps.put(4, finishQuest);

		steps.put(5, finishQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		knife = new ItemRequirement("Knife", ItemID.KNIFE).isNotConsumed();
		bronzeAxe = new ItemRequirement("Bronze axe", ItemID.BRONZE_AXE).isNotConsumed();
		axe = new ItemRequirement("Any axe", ItemID.BRONZE_AXE).isNotConsumed();
		axe.addAlternates(ItemCollections.AXES);
		combatGear = new ItemRequirement("Runes, or a way of dealing damage which you can smuggle onto Entrana. Runes for Crumble Undead (level 39 Magic) are best", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(ItemID.SKULL);
		teleport = new ItemRequirement("Teleport, preferably to Lumbridge. Home teleport will work if off cooldown", ItemID.RING_OF_ELEMENTS_CHARGED);
		teleport.addAlternates(ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		dramenBranch = new ItemRequirement("Dramen branch", ItemID.DRAMEN_BRANCH);
		dramenStaff = new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF);
		dramenStaffEquipped = new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF, 1, true);
	}

	@Override
	protected void setupZones()
	{
		entrana = new Zone(new WorldPoint(2798, 3327, 0), new WorldPoint(2878, 3394, 1));
		entranaDungeon = new Zone(new WorldPoint(2817, 9722, 0), new WorldPoint(2879, 9784, 0));
	}

	public void setupConditions()
	{
		onEntrana = new ZoneRequirement(entrana);
		inDungeon = new ZoneRequirement(entranaDungeon);
		shamusNearby = new NpcCondition(NpcID.ZANARISLEPRECHAUN);
		bronzeAxeNearby = new ItemOnTileRequirement(ItemID.BRONZE_AXE);
		dramenSpiritNearby = new NpcCondition(NpcID.TREE_SPIRIT);
	}

	public void setupSteps()
	{
		talkToWarrior = new NpcStep(this, NpcID.WARRIORADVENTURERPG, new WorldPoint(3151, 3207, 0), "Talk to the Warrior south east of Draynor Village.");
		talkToWarrior.addDialogStep("What are you camped out here for?");
		talkToWarrior.addDialogStep("What makes you think it's out here?");
		talkToWarrior.addDialogStep("If it's hidden how are you planning to find it?");
		talkToWarrior.addDialogStep("Looks like you don't know either.");
		chopTree = new ObjectStep(this, ObjectID.LEPRECHAUNTREE, new WorldPoint(3139, 3213, 0), "Try cutting the tree just to the west.", axe);
		chopTree.addDialogStep("I've been in that shed, I didn't see a city.");
		talkToShamus = new NpcStep(this, NpcID.ZANARISLEPRECHAUN, new WorldPoint(3138, 3212, 0), "Talk to Shamus.");
		talkToShamus.addDialogStep("I've been in that shed, I didn't see a city.");
		goToEntrana = new NpcStep(this, NpcID.SHIPMONK1_C, new WorldPoint(3047, 3236, 0), "Bank all weapons and armour you have (including the axe), and go to Port Sarim to get a boat to Entrana.", combatGear);
		goDownHole = new ObjectStep(this, ObjectID.ENTRANALADDERTOP, new WorldPoint(2820, 3374, 0), "Climb down the ladder on the north side of the island. Once you go down, you can only escape via teleport.");
		goDownHole.addDialogStep("Well that is a risk I will have to take.");
		getAxe = new DetailedQuestStep(this, new WorldPoint(2843, 9760, 0), "Kill zombies until one drops a bronze axe.");
		pickupAxe = new DetailedQuestStep(this, "Pick up the bronze axe", bronzeAxe);
		attemptToCutDramen = new ObjectStep(this, ObjectID.DRAMENTREE, new WorldPoint(2861, 9735, 0), "Attempt to cut a branch from the Dramen tree. Be prepared for a Tree Spirit (level 101) to appear, which you can safespot behind nearby fungus.", bronzeAxe);
		killDramenSpirit = new NpcStep(this, NpcID.TREE_SPIRIT, new WorldPoint(2859, 9734, 0), "Kill the Tree Spirit. They can be safespotted behind nearby fungi to the east.");
		cutDramenBranch = new ObjectStep(this, ObjectID.DRAMENTREE, new WorldPoint(2861, 9735, 0), "Cut at least one branch from the Dramen tree. It's recommended you cut at least 4 branches so you don't have to return in future quests.");
		teleportAway = new DetailedQuestStep(this, "Teleport away with the branches, preferably to Lumbridge.", dramenBranch);
		teleportAway.addTeleport(teleport);
		getAnotherBranch = new DetailedQuestStep(this, "If you've lost your Dramen branch/staff, you will need to return to Entrana and cut another. You will not need to defeat the Tree Spirit again.");
		craftBranch = new DetailedQuestStep(this, "Use a knife on the dramen branch to craft a dramen staff.", knife, dramenBranch);
		enterZanaris = new ObjectStep(this, ObjectID.ZANARISDOOR, new WorldPoint(3202, 3169, 0), "Enter the shed south of Lumbridge with your Dramen Staff equipped.", dramenStaffEquipped);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Multiple zombies (level 25) (can be safespotted)");
		reqs.add("Dramen Tree Spirit (level 101) (can be safespotted)");
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(axe);
		reqs.add(knife);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(combatGear);
		reqs.add(teleport);
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.CRAFTING, 31, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 36, true));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Zanaris."),
				new UnlockReward("Ability to craft Cosmic Runes"),
				new UnlockReward("Ability to buy and wield Dragon Daggers & Longswords"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToWarrior), axe));
		allSteps.add(new PanelDetails("Finding Shamus", Arrays.asList(chopTree, talkToShamus)));
		allSteps.add(new PanelDetails("Getting a Dramen branch", Arrays.asList(goToEntrana, goDownHole, getAxe, attemptToCutDramen, killDramenSpirit,
				cutDramenBranch, teleportAway), List.of(), List.of(combatGear, teleport)));
		allSteps.add(new PanelDetails("Entering Zanaris", Arrays.asList(craftBranch, enterZanaris), knife));

		return allSteps;
	}
}
