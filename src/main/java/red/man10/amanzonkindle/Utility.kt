package red.man10.amanzonkindle

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.sql.ResultSet
import java.text.SimpleDateFormat

class Utility {

    fun itemFromBase64(data: String): ItemStack? {
        try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            val items = arrayOfNulls<ItemStack>(dataInput.readInt())

            // Read the serialized inventory
            for (i in items.indices) {
                items[i] = dataInput.readObject() as ItemStack
            }

            dataInput.close()
            return items[0]
        } catch (e: Exception) {
            return null
        }

    }

    @Throws(IllegalStateException::class)
    fun itemToBase64(item: ItemStack): String {
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            val items = arrayOfNulls<ItemStack>(1)
            items[0] = item
            dataOutput.writeInt(items.size)

            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }

            dataOutput.close()
            val base64: String = Base64Coder.encodeLines(outputStream.toByteArray())

            return base64

        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }


    }

    fun format(double: Double):String{
        return String.format("%,.0f",double)
    }

    fun formattedTimestamp(timestamp: java.sql.Timestamp, timeFormat: String): String {
        return SimpleDateFormat(timeFormat).format(timestamp)
    }

    fun createBookList(rs: ResultSet): MutableList<ItemStack>{

        val list = mutableListOf<ItemStack>()

        while (rs.next()){

            val book = itemFromBase64(rs.getString("item"))

            val meta = book!!.itemMeta
            val lore = mutableListOf<String>()

            val price = rs.getDouble("price")
            lore.add("??6??????: ${format(price)}???")

            val dl = rs.getInt("sold_amount")
            lore.add("??bDL???: ${dl}DL")

            val fav = rs.getInt("likes")
            lore.add("??d?????????!???: ${fav}?????????!")

            val cate = rs.getString("category")
            lore.add("??e${cate}")

            val date = rs.getTimestamp("date")!!
            val format = "yyyy???MM???dd??? HH???mm???"
            lore.add("??a${formattedTimestamp(date, format)}")

            val pub = rs.getBoolean("public")
            if (!pub){

                lore.add("??c?????????")

            }


            meta!!.lore = lore

            book.itemMeta = meta

            list.add(book)

        }

        rs.close()

        return list

    }


}