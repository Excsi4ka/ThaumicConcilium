package com.ilya3point999k.thaumicconcilium.api;

import com.ilya3point999k.thaumicconcilium.common.integration.minetweaker.TweakerHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.ArrayList;

public class ChainedRiftRecipe {

    private final ItemStack recipeOutput;

    public Object catalyst;
    public AspectList aspects;
    public String key;

    public int hash;

    public ChainedRiftRecipe(String researchKey, ItemStack result, Object cat, AspectList tags) {
        recipeOutput = result;
        this.aspects = tags;
        this.key = researchKey;
        this.catalyst = cat;
        if (cat instanceof String) {
            this.catalyst = OreDictionary.getOres((String) cat);
        }
        String hc = researchKey + result.toString();
        for (Aspect tag:tags.getAspects()) {
            hc += tag.getTag()+tags.getAmount(tag);
        }
        if (cat instanceof ItemStack) {
            hc += ((ItemStack)cat).toString();
        } else
        if (cat instanceof ArrayList && ((ArrayList<ItemStack>)catalyst).size()>0) {
            for (ItemStack is :(ArrayList<ItemStack>)catalyst) {
                hc += is.toString();
            }
        }

        hash = hc.hashCode();
    }



    public boolean matches(AspectList itags, ItemStack cat) {
        if (catalyst instanceof ItemStack &&
                !ThaumcraftApiHelper.itemMatches((ItemStack) catalyst,cat,false)) {
            return false;
        } else
        if (catalyst instanceof ArrayList && ((ArrayList<ItemStack>)catalyst).size()>0) {
            ItemStack[] ores = ((ArrayList<ItemStack>)catalyst).toArray(new ItemStack[]{});
            if (!ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{cat},ores)) return false;
        }
        if (itags==null) return false;
        for (Aspect tag:aspects.getAspects()) {
            if (itags.getAmount(tag)<aspects.getAmount(tag)) return false;
        }
        return true;
    }

    public boolean catalystMatches(ItemStack cat) {
        if (catalyst instanceof ItemStack && ThaumcraftApiHelper.itemMatches((ItemStack) catalyst,cat,false)) {
            return true;
        } else
        if (catalyst instanceof ArrayList && ((ArrayList<ItemStack>)catalyst).size()>0) {
            ItemStack[] ores = ((ArrayList<ItemStack>)catalyst).toArray(new ItemStack[]{});
            return ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{cat}, ores);
        }
        return false;
    }

    public AspectList removeMatching(AspectList itags) {
        AspectList temptags = new AspectList();
        temptags.aspects.putAll(itags.aspects);

        for (Aspect tag:aspects.getAspects()) {
            temptags.remove(tag, aspects.getAmount(tag));
        }

        itags = temptags;
        return itags;
    }

    public ItemStack getRecipeOutput() {
        return recipeOutput;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ChainedRiftRecipe) {
            ChainedRiftRecipe recipe = (ChainedRiftRecipe) o;
            return key.equals(recipe.key) && catalyst.equals(recipe.catalyst) && TweakerHelper.aspectsToString(aspects).equals(TweakerHelper.aspectsToString(recipe.aspects))
                    && ItemStack.areItemStacksEqual(recipeOutput,recipe.getRecipeOutput());
        }
        return false;
    }
}