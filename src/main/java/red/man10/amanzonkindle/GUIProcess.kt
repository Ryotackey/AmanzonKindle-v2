package red.man10.amanzonkindle

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.math.floor

class GUIProcess(val pl: AmanzonKindle) {

    fun menuGUI(): Inventory{

        val inv = pl.server.createInventory(null, 45, "§e§lA§d§lm§a§la§f§ln§e§lzonKindle")

        val brank = ItemStackA(Material.BLACK_STAINED_GLASS_PANE, 0, 1, "").build()
        for (i in 0 until inv.size){
            inv.setItem(i, brank)
        }

        val allbooks = ItemStackA(Material.BOOK, 0, 1, "§a§l出版されている本一覧").build()
        inv.setItem(11, allbooks)

        val title = ItemStackA(Material.OAK_SIGN, 0, 1, "§a§l題名で本を探す").build()
        inv.setItem(13, title)

        val author = ItemStackA(Material.NAME_TAG, 0, 1, "§a§l著者名で本を探す").build()
        inv.setItem(15, author)

        val dlrank = ItemStackA(Material.COMPASS, 0, 1, "§a§lDL数順で本を探す").build()
        inv.setItem(29, dlrank)

        val like = ItemStackA(Material.REDSTONE, 0, 1, "§a§lDL数順で本を探す").build()
        inv.setItem(30, like)

        val mypage = ItemStackA(Material.CHEST, 0, 1, "§a§lマイページ").build()
        inv.setItem(32, mypage)

        return inv

    }

    fun storeGUI(p: Player, page: Int){

        val list = pl.pagemap[p]

        if (list == null){
            p.sendMessage("${pl.prefix}§c本のデータが存在しません")
            return
        }

        val inv = pl.server.createInventory(null, 54, "§e§l本一覧")

        val next = ItemStackA(Material.WHITE_STAINED_GLASS_PANE, 1, 1, "§f次のページへ").build()
        val pre = ItemStackA(Material.WHITE_STAINED_GLASS_PANE, 1, 1, "§f前のページへ").build()
        val blank = ItemStackA(Material.BLACK_STAINED_GLASS_PANE, 1, 1, "").build()
        val pagei = ItemStackA(Material.YELLOW_STAINED_GLASS_PANE, 1, 1, page.toString()).build()

        for (i in 45 until 54){
            inv.setItem(i, blank)
        }

        inv.setItem(49, pagei)
        if (floor(list.size/45.0).toInt() != page) inv.setItem(53, next)
        if (page != 0) inv.setItem(45, pre)

        for (i in 0 until 45){

            if (list.size >= i+page*45) inv.setItem(i, list[i+page*45])

        }

        p.openInventory(inv)

        return

    }

}