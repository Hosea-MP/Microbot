/*
 *  * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.runelite.client.plugins.microbot.questhelper.requirements.item;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;

import java.util.List;

public class TeleportItemRequirement extends ItemRequirement
{
	public TeleportItemRequirement(String name, int id)
	{
		super(name, id);
		setChargedItem(true);
	}

	public TeleportItemRequirement(String name, int id, int quantity)
	{
		super(name, id, quantity);
		setChargedItem(true);
	}

	public TeleportItemRequirement(String name, ItemCollections itemCollection)
	{
		super(name, itemCollection);
		setChargedItem(true);
	}

	public TeleportItemRequirement(String name, List<Integer> items)
	{
		super(name, items);
		setChargedItem(true);
	}

	public TeleportItemRequirement(String name, List<Integer> items, int quantity)
	{
		super(name, items, quantity);
		setChargedItem(true);
	}

	public TeleportItemRequirement(String name, ItemCollections itemCollection, int quantity)
	{
		super(name, itemCollection, quantity);
		setChargedItem(true);
	}

	@Override
	protected TeleportItemRequirement copyOfClass()
	{
		return new TeleportItemRequirement(getName(), getId());
	}
}
