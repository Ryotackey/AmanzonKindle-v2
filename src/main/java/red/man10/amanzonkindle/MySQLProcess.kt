package red.man10.amanzonkindle

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class PublishBook(val pl: AmanzonKindle, val p: Player, val price: Double, val cate: String, val book: ItemStack): Thread() {

    override fun run() {

        val base64 = pl.itemToBase64(book)

        val bmeta = book.itemMeta as BookMeta

        val rs = pl.mysql!!.query("SELECT count(1) FROM kindle_library where item='$base64' and public=1;")?: return

        rs.next()

        val count = rs.getInt("count(1)")

        if (count != 0){
            p.sendMessage("${pl.prefix}§cすでにその本は出版されてます。")
            return
        }

        rs.close()

        pl.mysql!!.execute("INSERT INTO `kindle_library` (`title`, `price`, `author_name`, `author_uuid`, `item`, " +
                "`category`, `sold_amount`, `review`, `public`) " +
                "VALUES ('${bmeta.title}', '$price', '${p.name}', '${p.uniqueId}', '$base64', '$cate', 0, 0, true);")

        p.sendMessage("${pl.prefix}§a出版しました。")
        Bukkit.broadcastMessage("${pl.prefix}§e${p.displayName}§aが§e${bmeta.title}§aを出版しました。")

        return

    }

}

class SearchBook(val pl: AmanzonKindle, val p: Player, val bypass: Boolean, val column: String, val key: String): Thread(){

    override fun run() {

        val rs = if (bypass){
            pl.mysql!!.query("SELECT count(1) FROM kindle_library where $column='$key';")?: return
        }else{
            pl.mysql!!.query("SELECT count(1) FROM kindle_library where public=true and $column='$key';")?: return
        }

        pl.pagemap[p] = pl.createBookList(rs)

        val gui = GUIProcess(pl)
        gui.storeGUI(p, 0)

    }

}

class GetBook(val pl: AmanzonKindle, val p: Player, val bypass: Boolean): Thread(){

    override fun run() {

        val rs = if (bypass){
            pl.mysql!!.query("SELECT count(1) FROM kindle_library;")?: return
        }else{
            pl.mysql!!.query("SELECT count(1) FROM kindle_library where public=true;")?: return
        }

        pl.pagemap[p] = pl.createBookList(rs)

        val gui = GUIProcess(pl)
        gui.storeGUI(p, 0)

        return

    }

}