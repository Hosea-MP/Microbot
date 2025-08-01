/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package net.runelite.client.plugins.microbot.questhelper.helpers.achievementdiaries.varrock;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.ComplexStateQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.ChatMessageRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.ComplexRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirements;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SpellbookRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicType;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Spellbook;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarplayerRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VarrockElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement sAtk4, sStr4, sDef4, torstol, natureRune, astralRune, earthRune, coins, mahoganyLog, cookingGuild, rawPie,
		runeBar, feather, hammer, essence, earthTali, runeDartTip;

	// Quests required
	Requirement dreamMentor, runeMyster, touristTrap;

	Requirement notSuperCombat, notPlankMake, notSummerPie, notRuneDart, not100Earth, madeTips, lunarBook;

	QuestStep claimReward, moveToBank, superCombat, moveToLumb, plankMake, moveToCookingGuild, summerPie,
		moveToEarthRune, earthRune100, dartTip, runeDart, moveToAnvil;

	Zone bank, lumb, cGuild, earth, anvil;

	ZoneRequirement inBank, inLumb, inCookingGuild, inEarth, inAnvil;

	ConditionalStep superCombatTask, summerPieTask, runeDartTask, plankMakeTask, earthRuneTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		superCombatTask = new ConditionalStep(this, moveToBank);
		superCombatTask.addStep(inBank, superCombat);
		doElite.addStep(notSuperCombat, superCombatTask);

		summerPieTask = new ConditionalStep(this, moveToCookingGuild);
		summerPieTask.addStep(inCookingGuild, summerPie);
		doElite.addStep(notSummerPie, summerPieTask);

		runeDartTask = new ConditionalStep(this, moveToAnvil);
		runeDartTask.addStep(inAnvil, dartTip);
		runeDartTask.addStep(new Conditions(runeDartTip, madeTips), runeDart);
		doElite.addStep(notRuneDart, runeDartTask);

		plankMakeTask = new ConditionalStep(this, moveToLumb);
		plankMakeTask.addStep(inLumb, plankMake);
		doElite.addStep(notPlankMake, plankMakeTask);

		earthRuneTask = new ConditionalStep(this, moveToEarthRune);
		earthRuneTask.addStep(inEarth, earthRune100);
		doElite.addStep(not100Earth, earthRuneTask);

		return doElite;
	}

	@Override
	protected void setupRequirements()
	{
		notSuperCombat = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY2, false, 7);
		notPlankMake = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY2, false, 8);
		notSummerPie = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY2, false, 9);
		notRuneDart = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY2, false, 10);
		not100Earth = new VarplayerRequirement(VarPlayerID.VARROCK_ACHIEVEMENT_DIARY2, false, 11);

		lunarBook = new SpellbookRequirement(Spellbook.LUNAR);

		sAtk4 = new ItemRequirement("Super attack(4)", ItemID._4DOSE2ATTACK).showConditioned(notSuperCombat);
		sStr4 = new ItemRequirement("Super strength(4)", ItemID._4DOSE2STRENGTH).showConditioned(notSuperCombat);
		sDef4 = new ItemRequirement("Super defense(4)", ItemID._4DOSE2DEFENSE).showConditioned(notSuperCombat);
		torstol = new ItemRequirement("Torstol", ItemID.TORSTOL).showConditioned(notSuperCombat);
		natureRune = new ItemRequirement("Nature rune", ItemID.NATURERUNE).showConditioned(notPlankMake);
		astralRune = new ItemRequirement("Astral rune", ItemID.ASTRALRUNE).showConditioned(notPlankMake);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTHRUNE).showConditioned(notPlankMake);
		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notPlankMake);
		mahoganyLog = new ItemRequirement("Mahogany logs", ItemID.MAHOGANY_LOGS).showConditioned(notPlankMake);
		cookingGuild = new ItemRequirement("Access to cooking guild", ItemCollections.COOKING_GUILD).showConditioned(notSummerPie).isNotConsumed();
		cookingGuild.setTooltip("A chef's hat, Varrock Armour 3, or Cooking Cape");
		rawPie = new ItemRequirement("Raw summer pie", ItemID.UNCOOKED_SUMMER_PIE).showConditioned(notSummerPie);
		feather = new ItemRequirement("Feather", ItemID.FEATHER, 10).showConditioned(notRuneDart);
		runeBar = new ItemRequirement("Runite bar", ItemID.RUNITE_BAR).showConditioned(notRuneDart);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notRuneDart).isNotConsumed();
		runeDartTip = new ItemRequirement("Rune dart tip", ItemID.RUNE_DART_TIP);
		essence = new ItemRequirement("Essence", ItemCollections.ESSENCE_LOW).showConditioned(not100Earth);
		earthTali = new ItemRequirement("Access to the Earth Altar",
			ItemCollections.EARTH_ALTAR).showConditioned(not100Earth).isNotConsumed();
		earthTali.setTooltip("Earth Talisman/Tiara, Elemental Talisman/Tiara, RC-skill cape or via Abyss");

		inBank = new ZoneRequirement(bank);
		inLumb = new ZoneRequirement(lumb);
		inCookingGuild = new ZoneRequirement(cGuild);
		inAnvil = new ZoneRequirement(anvil);
		inEarth = new ZoneRequirement(earth);

		madeTips = new ChatMessageRequirement(
			inAnvil,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) madeTips).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inAnvil),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		dreamMentor = new QuestRequirement(QuestHelperQuest.DREAM_MENTOR, QuestState.FINISHED);
		runeMyster = new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED);
		touristTrap = new QuestRequirement(QuestHelperQuest.THE_TOURIST_TRAP, QuestState.FINISHED);
	}

	@Override
	protected void setupZones()
	{
		bank = new Zone(new WorldPoint(3179, 3448, 0), new WorldPoint(3191, 3432, 0));
		lumb = new Zone(new WorldPoint(3300, 3501, 0), new WorldPoint(3313, 3493, 0));
		cGuild = new Zone(new WorldPoint(3138, 3453, 0), new WorldPoint(3148, 3444, 0));
		earth = new Zone(new WorldPoint(2624, 4863, 0), new WorldPoint(2687, 4800, 0));
		anvil = new Zone(new WorldPoint(3185, 3427, 0), new WorldPoint(3190, 3420, 0));
	}

	public void setupSteps()
	{
		moveToBank = new DetailedQuestStep(this, new WorldPoint(3183, 3440, 0),
			"Move to the west Varrock bank.");
		superCombat = new ItemStep(this, "Create a super combat potion.", sAtk4.highlighted(), sStr4.highlighted(),
			sDef4.highlighted(), torstol.highlighted());
		moveToLumb = new ObjectStep(this, 2618, new WorldPoint(3308, 3492, 0),
			"Climb the fence to enter the lumber yard.", natureRune.quantity(20), astralRune.quantity(40),
			earthRune.quantity(300), coins.quantity(21000), mahoganyLog.quantity(20));
		plankMake = new DetailedQuestStep(this, "Cast plank make until you've made 20 mahogany planks.",
			natureRune.quantity(20), astralRune.quantity(40), earthRune.quantity(300), coins.quantity(21000), mahoganyLog.quantity(20));
		moveToCookingGuild = new ObjectStep(this, ObjectID.CHEFDOOR, new WorldPoint(3143, 3443, 0),
			"Enter the cooking guild.", cookingGuild.equipped());
		summerPie = new ObjectStep(this, ObjectID.FAI_VARROCK_RANGE, new WorldPoint(3146, 3453, 0),
			"Cook the summer pie.", rawPie);
		moveToEarthRune = new ObjectStep(this, 34816, new WorldPoint(3306, 3474, 0),
			"Travel to the earth altar or go through the abyss.", earthTali, essence.quantity(25));
		earthRune100 = new ObjectStep(this, 34763, new WorldPoint(2658, 4841, 0),
			"Craft the earth runes.", essence.quantity(25));
		moveToAnvil = new DetailedQuestStep(this, new WorldPoint(3188, 3426, 0),
			"Go to the anvil beside the west Varrock bank.", runeBar, feather, hammer);
		dartTip = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(3188, 3426, 0),
			"Make rune dart tips on the anvil in west Varrock.", runeBar);
		runeDart = new ItemStep(this, "Use feathers on the rune dart tips.", runeDartTip.highlighted(),
			feather.highlighted());

		claimReward = new NpcStep(this, NpcID.TOBY_VARROCK_DIARY, new WorldPoint(3225, 3415, 0),
			"Talk to Toby in Varrock to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(sAtk4, sStr4, sDef4, torstol, natureRune.quantity(20), astralRune.quantity(40),
			earthRune.quantity(300), coins.quantity(21000), mahoganyLog.quantity(20), cookingGuild, rawPie, runeBar,
			feather, hammer, essence.quantity(25), earthTali);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.COOKING, 95, true));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 81, true));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 90, true));
		reqs.add(new SkillRequirement(Skill.MAGIC, 86, true));
		reqs.add(new ComplexRequirement(LogicType.OR, "78 Runecraft or 52 with Raiments of the Eye set",
			new SkillRequirement(Skill.RUNECRAFT, 78, true, "78 Runecraft"),
			new ItemRequirements("52 with Raiments of the Eye set",
				new ItemRequirement("Hat", ItemCollections.EYE_HAT),
				new ItemRequirement("Top", ItemCollections.EYE_TOP),
				new ItemRequirement("Bottom", ItemCollections.EYE_BOTTOM),
				new ItemRequirement("Boot", ItemID.BOOTS_OF_THE_EYE))
		));
		reqs.add(new SkillRequirement(Skill.SMITHING, 89, true));

		reqs.add(dreamMentor);
		reqs.add(touristTrap);
		reqs.add(runeMyster);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Varrock Armor (4)", ItemID.VARROCK_ARMOUR_ELITE, 1),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.THOSF_REWARD_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("10% Chance to mine 2 ores at once, up to and including Amethyst"),
			new UnlockReward("10% Chance of smelting 2 bars at once when using the Edgeville furnace"),
			new UnlockReward("Zaff will sell 120 Battlestaves per day for 7,000 Coins each"),
			new UnlockReward("The Skull sceptre will now hold 26 charges"),
			new UnlockReward("Acts as a prospector jacket for experience bonus and clues"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails superCombatSteps = new PanelDetails("Make Super Combat", Arrays.asList(moveToBank, superCombat),
			new SkillRequirement(Skill.HERBLORE, 90, true), sAtk4, sStr4, sDef4, torstol);
		superCombatSteps.setDisplayCondition(notSuperCombat);
		superCombatSteps.setLockingStep(superCombatTask);
		allSteps.add(superCombatSteps);

		PanelDetails summerPieSteps = new PanelDetails("Summer Pie", Arrays.asList(moveToCookingGuild, summerPie),
			new SkillRequirement(Skill.COOKING, 95, true), cookingGuild, rawPie);
		summerPieSteps.setDisplayCondition(notSummerPie);
		summerPieSteps.setLockingStep(summerPieTask);
		allSteps.add(summerPieSteps);

		PanelDetails runeDartsSteps = new PanelDetails("Smith and Fletch 10 Rune Darts", Arrays.asList(moveToAnvil,
			dartTip, runeDart), new SkillRequirement(Skill.FLETCHING, 81, true), new SkillRequirement(Skill.SMITHING, 89, true),
			touristTrap, runeBar, feather, hammer);
		runeDartsSteps.setDisplayCondition(notRuneDart);
		runeDartsSteps.setLockingStep(runeDartTask);
		allSteps.add(runeDartsSteps);

		PanelDetails plankMakeSteps = new PanelDetails("Plank Make", Arrays.asList(moveToLumb, plankMake),
			new SkillRequirement(Skill.MAGIC, 86, true), lunarBook, dreamMentor, natureRune.quantity(20),
			astralRune.quantity(40), earthRune.quantity(300), coins.quantity(21000), mahoganyLog.quantity(20));
		plankMakeSteps.setDisplayCondition(notPlankMake);
		plankMakeSteps.setLockingStep(plankMakeTask);
		allSteps.add(plankMakeSteps);

		PanelDetails earth100Steps = new PanelDetails("Craft 100 Earth runes", Arrays.asList(moveToEarthRune,
			earthRune100), new SkillRequirement(Skill.RUNECRAFT, 78, true), essence.quantity(25), earthTali);
		earth100Steps.setDisplayCondition(not100Earth);
		earth100Steps.setLockingStep(earthRuneTask);
		allSteps.add(earth100Steps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
