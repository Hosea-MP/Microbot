/*
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.junglepotion;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class JunglePotion extends BasicQuestHelper
{
	//Items Required
	ItemRequirement grimySnakeWeed, snakeWeed, grimyArdrigal, ardrigal, grimySitoFoil, sitoFoil, grimyVolenciaMoss, volenciaMoss,
		roguesPurse, grimyRoguesPurse;

	QuestStep startQuest, finishQuest;

	ObjectStep getSnakeWeed, getArdrigal, getSitoFoil, getVolenciaMoss, enterCave, getRoguePurseHerb;

	ConditionalStep cleanAndReturnSnakeWeed, cleanAndReturnArdrigal, cleanAndReturnSitoFoil, cleanAndReturnVolenciaMoss,
		getRoguesPurse, cleanAndReturnRoguesPurse;

	ZoneRequirement isUnderground;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		return getSteps();
	}

	@Override
	protected void setupRequirements()
	{
		grimySnakeWeed = new ItemRequirement("Grimy Snake Weed", ItemID.UNIDENTIFIED_SNAKE_WEED);
		grimySnakeWeed.setHighlightInInventory(true);
		snakeWeed = new ItemRequirement("Snake Weed", ItemID.SNAKE_WEED);

		grimyArdrigal = new ItemRequirement("Grimy Ardrigal", ItemID.UNIDENTIFIED_ARDRIGAL);
		grimyArdrigal.setHighlightInInventory(true);
		ardrigal = new ItemRequirement("Ardrigal", ItemID.ARDRIGAL);

		grimySitoFoil = new ItemRequirement("Grimy Sito Foil", ItemID.UNIDENTIFIED_SITO_FOIL);
		grimySitoFoil.setHighlightInInventory(true);
		sitoFoil = new ItemRequirement("Sito Foil", ItemID.SITO_FOIL);

		grimyVolenciaMoss = new ItemRequirement("Grimy Volencia Moss", ItemID.UNIDENTIFIED_VOLENCIA_MOSS);
		grimyVolenciaMoss.setHighlightInInventory(true);
		volenciaMoss = new ItemRequirement("Volencia Moss", ItemID.VOLENCIA_MOSS);

		grimyRoguesPurse = new ItemRequirement("Grimy Rogues Purse", ItemID.UNIDENTIFIED_ROGUES_PURSE);
		grimyRoguesPurse.setHighlightInInventory(true);
		roguesPurse = new ItemRequirement("Rogues Purse", ItemID.ROGUES_PURSE);
	}

	@Override
	protected void setupZones()
	{
		//2824,9462,0
		//2883, 9533, 0
		Zone undergroundZone = new Zone(new WorldPoint(2824, 9462, 0), new WorldPoint(2883, 9533, 0));
		isUnderground = new ZoneRequirement(undergroundZone);
	}

	private Map<Integer, QuestStep> getSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuestStep());
		steps.put(1, getSnakeWeed());
		steps.put(2, returnSnakeWeed());
		steps.put(3, getArdrigal());
		steps.put(4, returnArdigal());
		steps.put(5, getSitoFoil());
		steps.put(6, returnSitoFoil());
		steps.put(7, getVolenciaMoss());
		steps.put(8, returnVolenciaMoss());
		steps.put(9, getRoguesPurse());
		steps.put(10, returnRoguesPurse());
		steps.put(11, finishQuestStep());
		return steps;
	}

	private QuestStep startQuestStep()
	{
		startQuest = talkToTrufitus("Talk to Trufitus in Tai Bwo Wannai on Karamja.");
		startQuest.addDialogSteps("It's a nice village, where is everyone?");
		startQuest.addDialogSteps("Me? How can I help?");
		startQuest.addDialogSteps("It sounds like just the challenge for me.");
		return startQuest;
	}

	private QuestStep returnArdigal()
	{
		return cleanAndReturnArdrigal = getReturnHerbStep("Ardrigal", grimyArdrigal, ardrigal);
	}

	private QuestStep returnSnakeWeed()
	{
		return cleanAndReturnSnakeWeed = getReturnHerbStep("Snake Weed", grimySnakeWeed, snakeWeed);
	}

	private QuestStep returnSitoFoil()
	{
		return cleanAndReturnSitoFoil = getReturnHerbStep("Sito Foil", grimySitoFoil, sitoFoil);
	}

	private QuestStep returnVolenciaMoss()
	{
		return cleanAndReturnVolenciaMoss = getReturnHerbStep("Volencia Moss", grimyVolenciaMoss, volenciaMoss);
	}

	private QuestStep returnRoguesPurse()
	{
		cleanAndReturnRoguesPurse = getReturnHerbStep("Rogues Purse", grimyRoguesPurse, roguesPurse);
		cleanAndReturnRoguesPurse.addStep(isUnderground, new ObjectStep(this,
			ObjectID.JP_CAVEROCKSOUT, new WorldPoint(2830, 9522, 0), "Climb out of the cave."));
		return cleanAndReturnRoguesPurse;
	}

	private ConditionalStep getReturnHerbStep(String herbName, ItemRequirement grimyHerb, ItemRequirement cleanHerb)
	{
		NpcStep returnHerb = talkToTrufitus("", cleanHerb);
		returnHerb.addDialogSteps("Of course!");
		DetailedQuestStep cleanGrimyHerb = new DetailedQuestStep(this, "", grimyHerb);

		ConditionalStep cleanAndReturnHerb = new ConditionalStep(this, cleanGrimyHerb, "Clean and return the " + herbName + " to Trufitus.");
		cleanAndReturnHerb.addStep(cleanHerb, returnHerb);
		return cleanAndReturnHerb;
	}

	private QuestStep getSnakeWeed()
	{
		getSnakeWeed = new ObjectStep(this, ObjectID.SNAKE_VINE_FULL, new WorldPoint(2763, 3044, 0),
			"Search a marshy jungle vine south of Tai Bwo Wannai for some snake weed.");
		getSnakeWeed.addText("If you want to do Zogre Flesh Eaters or Legends' Quest grab one for each as you will need them later.");
		return getSnakeWeed;
	}

	private QuestStep getArdrigal()
	{
		getArdrigal = new ObjectStep(this, ObjectID.ARDRIGAL_PALM_FULL, new WorldPoint(2871, 3116, 0),
			"Search the palm trees north east of Tai Bwo Wannai for an Ardrigal herb.");
		getArdrigal.addText("If you want to do Legends' Quest grab one extra as you will need it later.");
		return getArdrigal;
	}

	private QuestStep getSitoFoil()
	{
		return getSitoFoil = new ObjectStep(this, ObjectID.SITO_SOIL_FULL, new WorldPoint(2791, 3047, 0),
			"Search the scorched earth in the south of Tai Bwo Wannai for a Sito Foil herb.");
	}

	private QuestStep getVolenciaMoss()
	{
		getVolenciaMoss = new ObjectStep(this, ObjectID.VOLENCIA_MOSS_ROCK_FULL, new WorldPoint(2851, 3036, 0),
			"Search the rock for a Volencia Moss herb at the mine south east of Tai Bwo Wannai.");
		getVolenciaMoss.addText("If you plan on doing Fairy Tale I then take an extra.");
		return getVolenciaMoss;
	}

	private QuestStep getRoguesPurse()
	{
		enterCave = new ObjectStep(this, ObjectID.POTHOLE_CAVE_ENTRANCE, new WorldPoint(2825, 3119, 0),
			"Enter the cave to the north by clicking on the rocks.");
		enterCave.addDialogStep("Yes, I'll enter the cave.");
		getRoguePurseHerb = new ObjectStep(this, ObjectID.ROGUES_PURSE_CAVE_FULL, "Get the Rogues Purse from the fungus covered " +
			"wall in the underground dungeon.");
		getRoguePurseHerb.setHideWorldArrow(true);
		getRoguePurseHerb.addText("If you are planning on doing Zogre Flesh Eaters then take an extra.");

		getRoguesPurse = new ConditionalStep(this, enterCave);
		getRoguesPurse.addStep(isUnderground, getRoguePurseHerb);

		getRoguesPurse.addSubSteps(enterCave);
		return getRoguesPurse;
	}

	private QuestStep finishQuestStep()
	{
		finishQuest = talkToTrufitus("Talk to Trufitus to finish the quest.");
		return finishQuest;
	}

	private NpcStep talkToTrufitus(String text, Requirement... requirements)
	{
		return new NpcStep(this, NpcID.TRUFITUS, new WorldPoint(2809, 3085, 0), text, requirements);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Survive against level 53 Jogres and level 46 Harpie Bug Swarms.");
		return reqs;
	}

	//Recommended
	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		ItemRequirement food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		reqs.add(food);
		reqs.add(new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS));
		ItemRequirement karaTele = new ItemRequirement("Teleport to Karamja (Glory/house teleport)",
			ItemID.NZONE_TELETAB_BRIMHAVEN);
		karaTele.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		reqs.add(karaTele);
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.HERBLORE, 3, false));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.HERBLORE, 775));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> steps = new ArrayList<>();

		PanelDetails startingPanel = new PanelDetails("Starting quest",
			Collections.singletonList(startQuest));
		steps.add(startingPanel);

		PanelDetails snakeWeedPanel = new PanelDetails("Snake Weed",
			Arrays.asList(getSnakeWeed, cleanAndReturnSnakeWeed));
		steps.add(snakeWeedPanel);

		PanelDetails ardrigalPanel = new PanelDetails("Ardrigal",
			Arrays.asList(getArdrigal, cleanAndReturnArdrigal));
		steps.add(ardrigalPanel);

		PanelDetails sitoFoilpanel = new PanelDetails("Sito Foil",
			Arrays.asList(getSitoFoil, cleanAndReturnSitoFoil));
		steps.add(sitoFoilpanel);

		PanelDetails volenciaMossPanel = new PanelDetails("Volencia Moss",
			Arrays.asList(getVolenciaMoss, cleanAndReturnVolenciaMoss));
		steps.add(volenciaMossPanel);

		PanelDetails roguesPursePanel = new PanelDetails("Rogues Purse",
			Arrays.asList(enterCave, getRoguePurseHerb, cleanAndReturnRoguesPurse));
		steps.add(roguesPursePanel);

		return steps;
	}
}
