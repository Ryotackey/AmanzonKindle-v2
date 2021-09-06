package red.man10.amanzonkindle

import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class PublishBook(val pl: AmanzonKindle, val p: Player, val price: Double, val cate: String, val book: ItemStack): Thread() {

    override fun run() {

        val base64 = pl.util.itemToBase64(book)

        val bmeta = book.itemMeta as BookMeta

        val rs = pl.mysql!!.query("SELECT count(1) FROM kindle_library where item='$base64' and public=1;")?: return

        rs.next()

        val count = rs.getInt("count(1)")

        if (count != 0){
            p.sendMessage("${pl.prefix}§cすでにその本は出版されてます。")
            return
        }

        rs.close()

        if (pl.v!!.getBalance(p.uniqueId) < pl.cost){
            p.sendMessage("${pl.prefix}§cお金が足りないので出版できません。(手数料: ${pl.util.format(pl.cost)}円)")
            return
        }

        pl.v!!.withdraw(p.uniqueId, pl.cost)

        pl.mysql!!.execute("INSERT INTO `kindle_library` (`title`, `price`, `author`, `author_uuid`, `item`, " +
                "`category`, `sold_amount`, `likes`, `public`) " +
                "VALUES ('${bmeta.title}', '$price', '${p.name}', '${p.uniqueId}', '$base64', '$cate', 0, 0, true);")

        p.sendMessage("${pl.prefix}§a出版しました。")
        Bukkit.broadcastMessage("${pl.prefix}§e${p.displayName}§aが§e${bmeta.title}§aを出版しました。")

        return

    }

}

class SearchBook(val pl: AmanzonKindle, val p: Player, val bypass: Boolean, val column: String, val key: String): Thread(){

    override fun run() {

        val rs = if (bypass){
            pl.mysql!!.query("SELECT * FROM kindle_library where $column like '%$key%';")?: return
        }else{
            pl.mysql!!.query("SELECT * FROM kindle_library where public=true and $column like '%$key%';")?: return
        }

        pl.pagemap[p] = pl.util.createBookList(rs)

        object : BukkitRunnable(){

            override fun run() {
                pl.gui.pageGUI(p, 0, "§e§l本一覧")
            }

        }.runTask(pl)

    }

}

class GetBook(val pl: AmanzonKindle, val p: Player, val bypass: Boolean): Thread(){

    override fun run() {

        val rs = if (bypass){
            pl.mysql!!.query("SELECT * FROM kindle_library;")?: return
        }else{
            pl.mysql!!.query("SELECT * FROM kindle_library where public=true;")?: return
        }

        pl.pagemap[p] = pl.util.createBookList(rs)

        object : BukkitRunnable(){

            override fun run() {
                pl.gui.pageGUI(p, 0, "§e§l本一覧")
            }

        }.runTask(pl)

        return

    }

}

class BuyBook(val pl: AmanzonKindle, val p: Player, val item: ItemStack): Thread(){

    override fun run() {

        val book = item.clone()

        val lore = mutableListOf<String>()

        val meta = book.itemMeta
        meta!!.lore = lore
        book.itemMeta = meta

        val base64 = pl.util.itemToBase64(book)

        val rs = pl.mysql!!.query("SELECT * FROM kindle_library where item='$base64';")?: return

        rs.next()

        val id = rs.getInt("id")
        val price = rs.getDouble("price")
        val uuid = rs.getString("author_uuid")
        val title = rs.getString("title")
        val amount = rs.getInt("sold_amount")

        rs.close()

        if (pl.v!!.getBalance(p.uniqueId) < price){
            p.sendMessage("${pl.prefix}§c電子マネーが足りません。")
            return
        }

        val count = pl.mysql!!.query("select count(1) from kindle_user where book_id=$id and uuid='${p.uniqueId}';")?: return

        count.next()

        if (count.getInt("count(1)") != 0) {
            p.sendMessage("${pl.prefix}§cあなたはすでにその本を持っています。")
            return
        }

        count.close()

        pl.mysql!!.execute("INSERT INTO `kindle_user` (`player`, `uuid`, `book_id`, `likes`) " +
                "VALUES ('${p.name}', '${p.uniqueId}', '${id}', 0);")
        pl.mysql!!.execute("UPDATE kindle_library SET sold_amount='${amount+1}' WHERE (id='$id');")

        pl.v!!.withdraw(p.uniqueId, price)

        pl.bank!!.deposit(UUID.fromString(uuid), price, "「$title」の印税")

        return

    }

}

class GetOwnBook(val pl: AmanzonKindle, val p: Player): Thread(){

    override fun run() {

        val rs = pl.mysql!!.query("select * from kindle_user where uuid='${p.uniqueId}';")?: return

        val list = mutableListOf<ItemStack>()

        while (rs.next()){

            val id = rs.getInt("book_id")

            val bookrs = pl.mysql !!.query("select * from kindle_library where id=$id;")?: return
            bookrs.next()

            val public = bookrs.getBoolean("public")

            if (!public && !p.hasPermission("amk.op")) continue

            val book = pl.util.itemFromBase64(bookrs.getString("item"))?: return
            val meta = book.itemMeta!!
            val lore = mutableListOf<String>()

            val dl = bookrs.getInt("sold_amount")
            lore.add("§bDL数: ${dl}DL")

            val fav = bookrs.getInt("likes")
            lore.add("§dいいね!数: ${fav}いいね!")

            val cate = bookrs.getString("category")
            lore.add("§e${cate}")

            if (rs.getBoolean("likes")){
                lore.add("§dいいね！済")
            }else{
                lore.add("§c未いいね！")
            }
            meta.lore = lore
            book.itemMeta = meta

            list.add(book)

            bookrs.close()

        }

        rs.close()

        pl.pagemap[p] = list

        object : BukkitRunnable(){

            override fun run() {
                pl.gui.pageGUI(p, 0, "§e§l本棚")
            }

        }.runTask(pl)

    }

}

class LikeProcess(val pl: AmanzonKindle, val p: Player, val item: ItemStack, val like: Boolean): Thread(){

    override fun run() {

        val book = item.clone()
        val meta = book.itemMeta!!
        meta.lore = mutableListOf()
        book.itemMeta = meta

        val base64 = pl.util.itemToBase64(book)

        val rs = pl.mysql!!.query("SELECT * FROM kindle_library where item='$base64';")?: return

        rs.next()

        val id = rs.getInt("id")
        val likes = rs.getInt("likes")

        rs.close()

        val item1 = item.clone()
        val meta1 = item1.itemMeta!!
        val lore = meta1.lore


        if (like){
            pl.mysql!!.execute("UPDATE kindle_user SET likes=true WHERE (book_id='$id' and uuid='${p.uniqueId}');")
            pl.mysql!!.execute("UPDATE kindle_library SET likes='${likes+1}' WHERE (id='$id');")
            p.sendMessage("${pl.prefix}§aいいね!しました。")
            lore!![3] = "§dいいね！済"
            lore[1] = ("§dいいね!数: ${likes+1}いいね!")
        }else{
            pl.mysql!!.execute("UPDATE kindle_user SET likes=false WHERE (book_id='$id');")
            pl.mysql!!.execute("UPDATE kindle_library SET likes='${likes-1}' WHERE (id='$id');")
            p.sendMessage("${pl.prefix}§cいいね!を解除しました。")
            lore!![3] = "§c未いいね！"
            lore[1] = ("§dいいね!数: ${likes-1}いいね!")
        }

        meta1.lore = lore
        item1.itemMeta = meta1

        object : BukkitRunnable(){

            override fun run() {
                p.closeInventory()
                p.openInventory(pl.gui.ownBookGUI(item1))
            }

        }.runTask(pl)

    }

}

class GetRankingBook(val pl: AmanzonKindle, val p: Player, val column: String, val bypass: Boolean): Thread(){

    override fun run() {

        val rs = if (bypass){
            pl.mysql!!.query("SELECT * FROM kindle_library ORDER BY $column DESC;")?: return
        }else{
            pl.mysql!!.query("SELECT * FROM kindle_library where public=true ORDER BY $column DESC;")?: return
        }

        pl.pagemap[p] = pl.util.createBookList(rs)

        object : BukkitRunnable(){

            override fun run() {
                pl.gui.pageGUI(p, 0, "§e§l本一覧")
            }

        }.runTask(pl)

        return

    }

}

class PublicateBook(val pl: AmanzonKindle, val p: Player, val item: ItemStack, val public: Boolean): Thread(){

    override fun run() {

        val book = item.clone()
        val meta = book.itemMeta!!
        meta.lore = mutableListOf()
        book.itemMeta = meta

        val base64 = pl.util.itemToBase64(book)

        if (public){
            val ex = pl.mysql!!.execute("UPDATE kindle_library SET public=${public} WHERE (item='$base64');")
            if (ex)p.sendMessage("${pl.prefix}§a公開しました。")
            else p.sendMessage("${pl.prefix}§c失敗しました。")
        }else{
            val ex = pl.mysql!!.execute("UPDATE kindle_library SET public=${public} WHERE (item='$base64');")
            if (ex)p.sendMessage("${pl.prefix}§c非公開にしました。")
            else p.sendMessage("${pl.prefix}§c失敗しました。")
        }


    }

}