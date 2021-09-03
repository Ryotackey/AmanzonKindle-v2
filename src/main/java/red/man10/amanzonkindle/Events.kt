package red.man10.amanzonkindle

import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.BookMeta

class Events(val pl: AmanzonKindle): Listener {

    @EventHandler
    fun clickInventory(e: InventoryClickEvent){

        val p = e.whoClicked as Player
        val title = e.view.title

        when (title){
            "§e§lA§d§lm§a§la§f§ln§e§lzonKindle"->{

                e.isCancelled = true

                val inv = e.inventory

                if (e.currentItem == null) return

                if (!e.currentItem!!.hasItemMeta()) return

                when(e.currentItem!!.itemMeta!!.displayName){

                    "§a§l出版されている本一覧"->{
                        p.closeInventory()
                        val getbook = GetBook(pl, p, p.hasPermission("amk.op"))
                        getbook.start()
                    }

                    "§a§l題名で本を探す"->{
                        p.closeInventory()
                        pl.gui.searchGUI(p, "title")
                    }

                    "§a§l著者名で本を探す"->{
                        p.closeInventory()
                        pl.gui.searchGUI(p, "author")
                    }

                    "§a§l本を出版する"->{
                        p.closeInventory()
                        if (!p.hasPermission("amk.publish")){
                            p.sendMessage("${pl.prefix}§cあなたには出版する権限がありません。")
                            p.closeInventory()
                            return
                        }

                        val book = p.inventory.itemInMainHand

                        if (book.type != Material.WRITTEN_BOOK){
                            p.sendMessage("${pl.prefix}§c出版する本を手に持ってください。")
                            p.closeInventory()
                            return
                        }

                        val bmeta = book.itemMeta as BookMeta

                        if (!bmeta.hasAuthor() || bmeta.author!! != p.name){
                            p.sendMessage("${pl.prefix}§c本を書いた本人しか出版できません。")
                            p.closeInventory()
                            return
                        }

                        if (bmeta.generation != null && bmeta.generation != BookMeta.Generation.ORIGINAL){
                            p.sendMessage("${pl.prefix}§cオリジナルの本しか出版できません。")
                            p.closeInventory()
                            return
                        }

                        pl.gui.categoryGUI(p, book)

                        return
                    }

                }

                return

            }

            "§e§l本一覧"->{

                e.isCancelled = true

                val inv = e.inventory
                val page = inv.getItem(49)!!.itemMeta!!.displayName.toInt()

                val item = e.currentItem

                if (e.currentItem == null) return

                if (!item!!.hasItemMeta()) return

                when(item.itemMeta!!.displayName){

                    "§f前のページへ"->{
                        pl.gui.storeGUI(p, page-1)
                    }

                    "§f次のページへ"->{
                        pl.gui.storeGUI(p, page+1)
                    }

                }

                if (item.type == Material.WRITTEN_BOOK){
                    p.closeInventory()
                    p.openInventory(pl.gui.buyGUI(item))
                }

                return

            }

            "§e§l購入確認"->{

                e.isCancelled = true

                val inv = e.inventory
                val book = inv.getItem(13)

                val item = e.currentItem

                if (e.currentItem == null) return

                if (!item!!.hasItemMeta()) return

                when(item.itemMeta!!.displayName){

                    "§a§l購入する"->{
                        val buy = BuyBook(pl, p, book!!)
                        buy.start()
                        p.closeInventory()
                    }

                    "§c§lやめる"->{
                        p.closeInventory()
                        pl.gui.storeGUI(p, 0)
                    }

                }

                return

            }

        }

    }

}