package CIG.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import CIG.Main.CustomItemGUI;
import CIG.Util.CustomDamageValue;
import CIG.Util.CustomDefanseValue;
import CIG.Util.MainConfiguration;
import CIG.Util.MainMessage;
import CIG.Util.data;

public class EntityDamagenbyEntity extends data implements Listener {

	private final CustomItemGUI plugin;

	public EntityDamagenbyEntity(CustomItemGUI plugin) {
		super(plugin);
		this.plugin = plugin;

	}

	double durability;

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Onweaponevent(EntityDamageByEntityEvent e) {

		Entity attacker = e.getDamager();
		Entity victim = e.getEntity();
		if (attacker instanceof Player) {
			Player p = (Player) attacker;
			ItemStack hand = p.getItemInHand();

			if (hand != null) {
				if (hand.getItemMeta() != null) {
					if (hand.getItemMeta().getDisplayName() != null) {
						String name = p.getItemInHand().getItemMeta().getDisplayName();
						ItemStack item = getitem().containsKey(ItemEquals(name)) ? hand : null;
						if (item != null) {
							CustomDamageValue cdv = weaponDamage(item);
							if (getStatsLores(item, getText("DURABILITY")) != -1) {
								durability = getLoreValue(item, getText("DURABILITY"),
										getStatsLores(item, getText("DURABILITY")));
								if (durability != 0) {

									int newdurablilty = (int) (durability - 1);

									ItemMeta itemMeta = item.getItemMeta();

									String value = setLoreValue(item, getText("DURABILITY"),
											getStatsLores(item, getText("DURABILITY")), newdurablilty);
									String newlore = MainConfiguration.getStatsIdentifierValueFormat();
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("stats", "DURABILITY");
									map.put("value", value);
									List<String> lore = itemMeta.getLore();
									lore.set(getStatsLores(item, getText("DURABILITY")),
											color(placeholder(map, newlore, "<", ">")));

									itemMeta.setLore(lore);
									item.setItemMeta(itemMeta);
								}
								if (durability == 0) {
									e.setCancelled(true);
									MainMessage.CUSTOMITEMGUI_ITEMBREAK(p);
									return;
								}
							}
							double customdamage = cdv.getDamage();
							double pvpdamage = cdv.getPvPDamage();
							double pvedamage = cdv.getPvEDamage();
							double aoedamage = cdv.getAttackAoEDamage();
							double critchance = cdv.getCriticalChance();
							double mathrandom = (Math.random() * 100) + 1;
							boolean chance = mathrandom <= critchance ? true : false;
							double bonusPercentDamage = isPlayer(victim) ? pvpdamage : pvedamage;
							double critdamage = chance ? (customdamage + pvpdamage) * (cdv.getCriticalDamage() / 100)
									: 0.0D;
							double damage = customdamage + bonusPercentDamage + critdamage;
							e.setDamage(damage);
							return;
						} else {
							return;
						}

					}
				}
			}
		}

	}

	@EventHandler
	public void Onequipevent(EntityDamageByEntityEvent e) {
		Entity victim = e.getEntity();
		Entity damager = e.getDamager();
		if (victim instanceof Player) {
			Player p = (Player) victim;

			CustomDefanseValue cdv = EquipDefense(p);

			double customdefense = cdv.getDefense();
			double pvpDefense = cdv.getPvPDefense();
			double pveDefense = cdv.getPvEDefense();
			double health = cdv.getHealth();
			double healthRegen = cdv.getHealthRegen();
			double blockAmount = cdv.getBlockAmount();
			double blockRate = cdv.getBlockRate();
			double dodgeRate = cdv.getDodgeRate();
			double bonusPercentdefense = isPlayer(damager) ? pvpDefense : pveDefense;
			double defense = bonusPercentdefense + customdefense;
			double mathrandom = (Math.random() * 100) + 1;
			boolean dodge = mathrandom <= dodgeRate ? true : false;
			double basicdamage = e.getDamage();
			if (dodgeRate != 0) {
				if (dodge) {
					e.setCancelled(true);
					return;
				}
			}
			double newdamage = basicdamage - defense <= 0 ? 0 : basicdamage - defense;
			e.setDamage(newdamage);
			p.sendMessage(basicdamage + "/" + defense + "=" + e.getDamage());
			return;

		}
	}

	public CustomItemGUI getPlugin() {
		return plugin;
	}

}
