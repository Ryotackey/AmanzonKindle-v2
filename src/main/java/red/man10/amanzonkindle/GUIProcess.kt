package red.man10.amanzonkindle

import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.lang.NumberFormatException
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
        inv.setItem(28, dlrank)

        val like = ItemStackA(Material.REDSTONE, 0, 1, "§a§lいいね!数順で本を探す").build()
        inv.setItem(30, like)

        val publish = ItemStackA(Material.DISPENSER, 1, 1, "§a§l本を出版する").build()
        inv.setItem(32, publish)

        //val mypage = ItemStackA(Material.CHEST, 0, 1, "§a§lマイページ").build()
        //inv.setItem(34, mypage)

        val bookshelf = ItemStackA(Material.BOOKSHELF, 0, 1, "§a§l本棚").build()
        inv.setItem(34, bookshelf)

        return inv

    }

    fun pageGUI(p: Player, page: Int, title: String){

        val list = pl.pagemap[p]

        if (list == null){
            p.sendMessage("${pl.prefix}§c本のデータが存在しません。")
            return
        }

        val inv = pl.server.createInventory(null, 54, title)

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

            if (list.size > i+page*45) inv.setItem(i, list[i+page*45])

        }

        p.openInventory(inv)

        return

    }

    fun buyGUI(book: ItemStack): Inventory{

        val inv = pl.server.createInventory(null, 45, "§e§l購入確認")

        val blank = ItemStackA(Material.BLACK_STAINED_GLASS_PANE, 1, 1, "").build()
        val accept = ItemStackA(Material.LIME_STAINED_GLASS_PANE, 1, 1, "§a§l購入する").build()
        val reject = ItemStackA(Material.RED_STAINED_GLASS_PANE, 1, 1, "§c§lやめる").build()

        for (i in 0 until inv.size){
            inv.setItem(i, blank)
        }

        inv.setItem(13, book)
        inv.setItem(30, accept)
        inv.setItem(32, reject)

        return inv

    }

    fun categoryGUI(p: Player, book: ItemStack){

        AnvilGUI.Builder()

            .onComplete { player: Player, text: String ->                                     //called when the inventory output slot is clicked

                if (!pl.catelist.contains(text))return@onComplete AnvilGUI.Response.text("カテゴリは存在しません")

                publishGUI(player, book, text)

                return@onComplete AnvilGUI.Response.close()

            }
            .text("カテゴリをここに入力") //sets the text the GUI should start with
            .itemLeft(ItemStack(Material.PAPER)) //use a custom item for the first slot
            .title("出版処理") //set the title of the GUI (only works in 1.14+)
            .plugin(pl) //set the plugin instance
            .open(p) //opens the GUI for the player provided

        return

    }

    fun publishGUI(p: Player, book: ItemStack, cate: String){

        AnvilGUI.Builder()

            .onComplete { player: Player, text: String ->                                     //called when the inventory output slot is clicked
                var price: Double? = null

                try {

                    price = text.toDouble()

                }catch (e: NumberFormatException){
                    return@onComplete AnvilGUI.Response.text("数字で入力してください")
                }

                val bmeta = book.itemMeta as BookMeta

                if (!bmeta.hasAuthor() || bmeta.author!! != player.name){
                    player.sendMessage("${pl.prefix}§c本を書いた本人しか出版できません。")
                    return@onComplete AnvilGUI.Response.close()
                }

                if (bmeta.generation != null && bmeta.generation != BookMeta.Generation.ORIGINAL){
                    player.sendMessage("${pl.prefix}§cオリジナルの本しか出版できません。")
                    return@onComplete AnvilGUI.Response.close()
                }

                val pb = PublishBook(pl, player, price, cate, book)
                pb.start()

                return@onComplete AnvilGUI.Response.close()

            }
            .text("値段をここに入力") //sets the text the GUI should start with
            .itemLeft(ItemStack(Material.WRITTEN_BOOK)) //use a custom item for the first slot
            .title("出版処理") //set the title of the GUI (only works in 1.14+)
            .plugin(pl) //set the plugin instance
            .open(p) //opens the GUI for the player provided

        return

    }

    fun searchGUI(p: Player, column: String){

        AnvilGUI.Builder()

            .onComplete { _: Player, text: String ->                                     //called when the inventory output slot is clicked

                val search = SearchBook(pl, p, p.hasPermission("amk.op"), column, text)
                search.start()

                return@onComplete AnvilGUI.Response.close()

            }
            .text("キーワードをここに入力") //sets the text the GUI should start with
            .itemLeft(ItemStack(Material.COMPASS)) //use a custom item for the first slot
            .title("本を検索する") //set the title of the GUI (only works in 1.14+)
            .plugin(pl) //set the plugin instance
            .open(p) //opens the GUI for the player provided

        return

    }

    fun ownBookGUI(book: ItemStack): Inventory{

        val inv = Bukkit.createInventory(null, 45, "§e§l本を読む")

        val like = book.itemMeta!!.lore!![0].contains("済")

        val blank = ItemStackA(Material.BLACK_STAINED_GLASS_PANE, 1, 1, "").build()
        val read = ItemStackA(Material.LIME_STAINED_GLASS_PANE, 1, 1, "§a§l本を読む").build()
        val likei = if (like) ItemStackA(Material.PINK_STAINED_GLASS_PANE, 1, 1, "§c§lいいね!を解除する").build()
                    else ItemStackA(Material.WHITE_STAINED_GLASS_PANE, 1, 1, "§d§lいいね!する").build()

        for (i in 0 until inv.size){
            inv.setItem(i, blank)
        }

        inv.setItem(13, book)
        inv.setItem(30, read)
        inv.setItem(32, likei)

        return inv

    }

}