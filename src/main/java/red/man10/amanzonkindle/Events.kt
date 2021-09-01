package red.man10.amanzonkindle

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

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
                        val getbook = GetBook(pl, p, p.hasPermission("amk.op"))
                        getbook.start()
                    }

                    "§a§l題名で本を探す"->{

                    }

                }

                return

            }

            "§e§l本一覧"->{

                e.isCancelled = true

                val inv = e.inventory
                val page = inv.getItem(49)!!.itemMeta!!.displayName.toInt()

                if (e.currentItem == null) return

                if (!e.currentItem!!.hasItemMeta()) return

                when(e.currentItem!!.itemMeta!!.displayName){

                    "§f前のページへ"->{
                        val gui = GUIProcess(pl)
                        gui.storeGUI(p, page-1)
                    }

                    "§f次のページへ"->{
                        val gui = GUIProcess(pl)
                        gui.storeGUI(p, page+1)
                    }

                }

                return

            }

        }



    }

}