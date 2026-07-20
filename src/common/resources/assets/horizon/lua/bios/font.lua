local __font_table = {}

local function loadFont()
    __font_table["chr_width"] = 5
    __font_table["chr_height"] = 5

    for chr = 0, 99 do
        __font_table[chr] = { chr = chr, x = ((chr % 10) * 6) + 2, y = (math.floor(chr / 10) * 6) + 2 }
    end

    local data = readEntry("font.png") -- readEntry defined in env from bios.lua
    return cc.imageio.fromBytes(data)
end

local __font_image = loadFont()

local function getGlyph(chr)
    if type(chr) == "string" then
        return getGlyph(string.byte(chr) - 32)
    end
    return __font_table[chr]
end

local function getGlyphs(str)
    local glyphs = {}
    for i = 1, #str do
        local chr = string.sub(str, i, i)
        table.insert(glyphs, getGlyph(chr))
    end
    return glyphs
end

return {
    __font_image = __font_image,
    getGlyph = getGlyph,
    getGlyphs = getGlyphs
}