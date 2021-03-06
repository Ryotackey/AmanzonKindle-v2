package red.man10.amanzonkindle

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemStackA(val mate: Material, val custom: Int=0, val amount: Int=1, var iname: String? = null, var lore: MutableList<String>? = null) {

    fun build(): ItemStack {

        val item = ItemStack(mate, amount)
        val meta = item.itemMeta
        meta!!.setDisplayName(iname)
        meta.lore = lore
        meta.setCustomModelData(custom)

        item.itemMeta = meta

        return item
    }

}