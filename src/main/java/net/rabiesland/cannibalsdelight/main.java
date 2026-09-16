package net.rabiesland.cannibalsdelight;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class main implements ModInitializer {

	public static final String modId = "cannibalsdelight";
    public static final Logger LOGGER = LoggerFactory.getLogger(modId);

	public static final Item RAW_HUMAN_MEAT_ITEM = registerItem(
			"raw_human_meat",
			Item::new,
			new Item.Properties().food(new FoodProperties.Builder()
					.nutrition(2)
					.saturationModifier(0.5F)
					.build(),
						Consumables.defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 20*30), 0.25F)).onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 20*30), 0.25F)).build())
		);
	public static final Item COOKED_HUMAN_MEAT_ITEM = registerItem(
			"cooked_human_meat",
			Item::new,
			new Item.Properties().food(new FoodProperties.Builder()
							.nutrition(5)
							.saturationModifier(0.75F)
							.build(),
					Consumables.defaultFood().build())
	);
	public static final Item MINCED_HUMAN_ITEM = registerItem(
			"minced_human",
			Item::new,
			new Item.Properties().food(new FoodProperties.Builder()
							.nutrition(1)
							.saturationModifier(0.5F)
							.build(),
					Consumables.defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 20*30), 0.25F)).onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 20*30), 0.25F)).build())
	);
	public static final Item HUMAN_PATTY_ITEM = registerItem(
			"human_patty",
			Item::new,
			new Item.Properties().food(new FoodProperties.Builder()
							.nutrition(5)
							.saturationModifier(0.75F)
							.build(),
					Consumables.defaultFood().build())
	);
	public static final Item HUMAN_BURGER_ITEM = registerItem(
			"human_burger",
			Item::new,
			new Item.Properties().food(new FoodProperties.Builder()
							.nutrition(15)
							.saturationModifier(0.9F)
							.build(),
					Consumables.defaultFood().build())
	);
	public static final Item PASTA_WITH_HUMAN_MEATBALLS_ITEM = registerItem(
			"pasta_with_human_meatballs",
			Item::new,
			new Item.Properties().food(new FoodProperties.Builder()
							.nutrition(20)
							.saturationModifier(1.0F)
							.build(),
					Consumables.defaultFood().build())
	);

	public static final CreativeModeTab ITEM_GROUP = FabricCreativeModeTab.builder()
			.icon(() -> new ItemStack(COOKED_HUMAN_MEAT_ITEM))
			.title(Component.translatable("itemGroup.cannibalsdelight.cannibalsdelight"))
			.displayItems((context, entries) -> {
				entries.accept(RAW_HUMAN_MEAT_ITEM);
				entries.accept(COOKED_HUMAN_MEAT_ITEM);
				entries.accept(MINCED_HUMAN_ITEM);
				entries.accept(HUMAN_PATTY_ITEM);
				entries.accept(HUMAN_BURGER_ITEM);
				entries.accept(PASTA_WITH_HUMAN_MEATBALLS_ITEM);
			})
			.build();

	public static final TagKey<Item> HUMAN_MEAT_KNIVES_ITAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(modId, "human_meat_knives"));
	public static final TagKey<EntityType<?>> HUMAN_ENTITIES_ETAG = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(modId, "human_entities"));

	@Override
	public void onInitialize() {
		LOGGER.info("Registering item groups");
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath("cannibalsdelight", "cannibalsdelight")), ITEM_GROUP);
		LOGGER.info("Registered.");


		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity, DamageSource) -> {
			if (entity.isAlwaysTicking()) { // if the killer is player
				Player player = (Player) entity; // get entity as player
				ItemStack mainhand = player.getInventory().getSelectedItem(); // get player's main hand item
				if (mainhand.is(HUMAN_MEAT_KNIVES_ITAG)) { // check the main hand item is in the cannibalsdelight:human_meat_knives item tag
					if (killedEntity.is(HUMAN_ENTITIES_ETAG)) { // check the killed entity is in the cannibalsdelight:human_entities entity type tag
						Vec3 pos = killedEntity.position(); // get the position of the killed entity
						ItemEntity e = new ItemEntity(world, pos.x, pos.y, pos.z, new ItemStack(RAW_HUMAN_MEAT_ITEM, (int)Math.floor(Math.random()*3+0.5))); // create the meat drop entity
						world.addFreshEntity(e); // spawn in the entity - i forgot to do this and was so confused lol
					}
				}
			}
		});
	}

	public static <T extends Item> T registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
		// Create the item key.
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("cannibalsdelight", name));

		// Create the item instance.
		T item = itemFactory.apply(settings.setId(itemKey));

		// Register the item.
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);

		return item;
	}
}