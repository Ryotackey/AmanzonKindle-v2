package red.man10.amanzonkindle

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BookMeta
import java.lang.NumberFormatException

class Commands(val pl: AmanzonKindle): CommandExecutor {

    override fun onCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (s !is Player) return true

        when(args.size){

            0->{

                val inv = pl.gui.menuGUI()
                s.openInventory(inv)

                return true

            }

            1->{

            }

            2->{

            }

            3->{

                when(args[0]){

                    "publish"->{

                        if (!s.hasPermission("amk.publish")) return true

                        var price: Double? = null

                        try {

                            price = args[1].toDouble()

                        }catch (e: NumberFormatException){
                            s.sendMessage("${pl.prefix}§c金額を数字で入力してください。")
                            return true
                        }

                        val cate = args[2]

                        if (!pl.catelist.contains(cate)){
                            s.sendMessage("${pl.prefix}§cそのカテゴリは存在しません。")
                            return true
                        }

                        if (s.inventory.itemInMainHand.type != Material.WRITTEN_BOOK){
                            s.sendMessage("${pl.prefix}§c出版する本を手に持ってください。")
                            return true
                        }

                        val book = s.inventory.itemInMainHand

                        val bmeta = book.itemMeta as BookMeta

                        if (!bmeta.hasAuthor() || bmeta.author!! != s.name){
                            s.sendMessage("${pl.prefix}§c本を書いた本人しか出版できません。")
                            return true
                        }

                        if (bmeta.generation != null && bmeta.generation != BookMeta.Generation.ORIGINAL){
                            s.sendMessage("${pl.prefix}§cオリジナルの本しか出版できません。")
                            return true
                        }

                        val pb = PublishBook(pl, s, price, cate, book)
                        pb.start()

                        return true

                    }

                    "search" -> {

                        val search = SearchBook(pl, s, s.hasPermission("amk.op"), args[1], args[2])
                        search.start()

                        return true

                    }

                }

            }

        }

        return false
    }

}