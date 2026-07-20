local ccstub = {
    bios = {
        chip = {}
    },
    peripherals = {
        internal = {}
    },
    imageio = {}
}

_G["cc"] = ccstub

function ccstub.bios.chip.readInt(pos) return 0 end
function ccstub.bios.chip.readShort(pos) return 0 end
function ccstub.bios.chip.readByte(pos) return 0 end
function ccstub.bios.chip.getBytes(pos, size) return {} end

function ccstub.peripherals.internal.findPeripherals(pId, pType) return {} end

function ccstub.imageio.fromBytes() return setmetatable({}, stub.image) end