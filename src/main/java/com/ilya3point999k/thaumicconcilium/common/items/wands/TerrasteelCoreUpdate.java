package com.ilya3point999k.thaumicconcilium.common.items.wands;

import fox.spiteful.forbidden.Config;
import fox.spiteful.forbidden.compat.Compat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.IWandRodOnUpdate;
import thaumcraft.common.items.wands.ItemWandCasting;
import vazkii.botania.api.mana.ManaItemHandler;

public class TerrasteelCoreUpdate implements IWandRodOnUpdate {

    Aspect primals[] = Aspect.getPrimalAspects().toArray(new Aspect[0]);

    public void onUpdate(ItemStack itemstack, EntityPlayer player)
    {
        if(Compat.botan && Config.crossWand && player.ticksExisted % 40 == 0 && checkHotbar(itemstack, player))
        {
            try
            {
                int cost = 20;

                for(int x = 0;x < primals.length;x++)
                {
                    int deficit = ((ItemWandCasting)itemstack.getItem()).getMaxVis(itemstack) - ((ItemWandCasting)itemstack.getItem()).getVis(itemstack, primals[x]);
                    if(deficit > 0)
                    {
                        deficit = Math.min(deficit, 100);
                        if(ManaItemHandler.requestManaExact(itemstack, player, cost * deficit, true))
                            ((ItemWandCasting)itemstack.getItem()).addVis(itemstack, primals[x], 1, true);
                    }
                }

            }
            catch(Throwable e)
            {
                return;
            }
        }

    }

    private boolean checkHotbar(ItemStack stack, EntityPlayer player)
    {
        for(int x = 0; x < 9; ++x)
        {
            ItemStack item = player.inventory.getStackInSlot(x);
            if(item == stack)
                return true;
        }
        return false;
    }

}