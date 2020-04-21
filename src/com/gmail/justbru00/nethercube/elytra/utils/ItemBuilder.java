package com.gmail.justbru00.nethercube.elytra.utils;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemBuilder {

	private ItemStack item;
	private ItemMeta itemMeta;
	private LeatherArmorMeta leatherMeta;
	
	/**
	 * Creates a new {@link ItemBuilder} object
	 * @param m The material the item should be.
	 */
	public ItemBuilder(Material m) {
		item = new ItemStack(m);
		itemMeta = item.getItemMeta();
		if (m.toString().toLowerCase().contains("leather")) {
			leatherMeta = (LeatherArmorMeta) item.getItemMeta();
		}
	}
	
	public ItemBuilder setAmount(int i) {
		item.setAmount(i);
		return this;
	}
	
	public ItemBuilder setDataValue(int i) {
		item.setDurability((short) i);
		return this;
	}
	
	public ItemBuilder setUnbreakable(boolean value) {
		if (item.getType().toString().toLowerCase().contains("leather")) {
			leatherMeta.setUnbreakable(value);
		} else {
			itemMeta.setUnbreakable(value);
		}
		return this;
	}
	
	public ItemBuilder setName(String name) {
		if (item.getType().toString().toLowerCase().contains("leather")) {
			leatherMeta.setDisplayName(Messager.color(name));
		} else {
			itemMeta.setDisplayName(Messager.color(name));
		}
		return this;
	}
	
	public ItemBuilder setLeatherArmorColor(Color c) {
		if (item.getType().toString().toLowerCase().contains("leather")) {
			leatherMeta.setColor(c);
		} 
		
		return this;
	}
	
	public ItemBuilder setFirstLoreLine(String lore) {
		if (item.getType().toString().toLowerCase().contains("leather")) {
			leatherMeta.setLore(Arrays.asList(Messager.color(lore)));
		} else {
			itemMeta.setLore(Arrays.asList(Messager.color(lore)));
		}
		return this;
	}
	
	public ItemStack build() {
		
		if (item.getType().toString().toLowerCase().contains("leather")) {
			item.setItemMeta(leatherMeta);
		} else {
			item.setItemMeta(itemMeta);
		}
		
		return item;
	}
	
}
