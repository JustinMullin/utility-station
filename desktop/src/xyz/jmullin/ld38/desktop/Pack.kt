package xyz.jmullin.ld38.desktop

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.tools.texturepacker.TexturePacker

fun main(args: Array<String>) {
    val resourcePath = "image"

    val settings = TexturePacker.Settings()
    settings.maxWidth = 2048
    settings.maxHeight = 2048
    settings.filterMin = Texture.TextureFilter.Linear
    settings.filterMag = Texture.TextureFilter.Linear
    settings.paddingX = 2
    settings.paddingY = 2
    settings.duplicatePadding = true

    TexturePacker.process(settings, resourcePath, "atlas", "ld38")
}