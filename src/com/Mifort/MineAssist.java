package com.Mifort;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mifort on 06.11.2016.
 */
public class MineAssist extends JavaPlugin implements Listener {

    List<AWaitBreak> aWaitBreaks = new ArrayList<>();

    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this,this);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(player==null)return;

        AWaitBreak aWaitBreak = AWaitBreak.get(player,aWaitBreaks);
        if(aWaitBreak == null)return;

        List<Block> blocks = getBlocks(aWaitBreak);

        for(Block block : blocks)
        {
            player.getWorld().dropItemNaturally(block.getLocation(),new ItemStack(block.getType()));
            block.setType(Material.AIR);
        }

        aWaitBreaks.remove(aWaitBreak);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        if(player==null)return;

        AWaitBreak aWaitBreak = AWaitBreak.get(player,aWaitBreaks);
        if(aWaitBreak != null)aWaitBreaks.remove(aWaitBreak);

        if(event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            aWaitBreaks.add(new AWaitBreak(player,event.getClickedBlock(),event.getBlockFace()));
        }
    }

    public List<Block> getBlocks(AWaitBreak aWaitBreak)
    {
        if(aWaitBreak.face == BlockFace.DOWN || aWaitBreak.face == BlockFace.UP)
            return getBlocks(aWaitBreak,1,0,1);
        else if(aWaitBreak.face == BlockFace.EAST || aWaitBreak.face == BlockFace.WEST)
            return getBlocks(aWaitBreak,0,1,1);
        else return getBlocks(aWaitBreak,1,1,0);
    }

    public List<Block> getBlocks(AWaitBreak aWaitBreak,int x,int y,int z)
    {
        List<Block> blocks = new ArrayList<>();

        World world = aWaitBreak.player.getWorld();

        int bX = aWaitBreak.block.getX();
        int bY = aWaitBreak.block.getY();
        int bZ = aWaitBreak.block.getZ();

        for(int cX = -x;cX <=x;cX++)
            for(int cY = -y;cY <=y;cY++)
                for(int cZ = -z;cZ <=z;cZ++)
                {
                    Block block = world.getBlockAt(bX + cX,bY + cY,bZ + cZ);
                    if(aWaitBreak.block == block)continue;
                    if(block.getType() == Material.AIR)continue;

                    blocks.add(block);
                }
        return blocks;
    }
}
