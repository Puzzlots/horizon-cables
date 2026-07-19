-- cc.bios.chip

local SUPERBLOCK_MAGIC = "HZFS" -- horizon filesystem
local SUPERBLOCK_VERSION = 1
local SUPERBLOCK_FORMAT = "<c4 B I2 I4 I4 I4 I4 I4 I4"          -- used for packing the superblock

local FAT_ENTRY_FREE = 0
local FAT_ENTRY_EOF = 0xFFFFFFFF

local DIR_NAME_MAX = 20 -- todo maybe change
local DIR_ENTRY_SIZE = 32 -- name: 20, type: 1, size: 4, firstSector: 4, + 2 pad
local DIR_ENTRY_FORMAT = "<c20 B i4 I4 xx"

local ENTRY_TYPE_FREE = 0
local ENTRY_TYPE_FILE = 1
local ENTRY_TYPE_DIR = 2
local ENTRY_TYPE_SYMLINK = 3 -- symbolic link linking to another folder/file entry

local fs = {}
fs.__index = fs



function fs:format(disk)
    local sectorSize = disk:getSectorSize()
    local totalSectors = disk:size()
    assert(totalSectors >= 3, "medium too small to hold a filesystem")

    local fatStartBlock = 1
    local remaining = totalSectors
    local entriesPerFatSect = sectorSize // 4


    
end