local tArgs = { ... }
local bootLoaderInfo = tArgs[1]
local biosApi = tArgs[0]
local mounts = require("Mounts") -- do something like this

local operatingSystemEntry = {
    storageDevice = nil,
    --initCodeSize = 0,
    --initCodeAddress = 0,
    osName = nil
}

local function split(str, delimiter)
    local result = {}
    -- Match one or more characters that are not the delimiter
    local pattern = "[^" .. delimiter .. "]+"
    for w in string.gmatch(str, pattern) do
        table.insert(result, w)
    end
    return result
end

local function loadSettings(fileString)
    local lines = split(fileString, "\n")
    for i = 1, #lines do
        if string.find(lines[i], "osName", 1, true) then
            local line = lines[i]:match("^%s*(.-)%s*$")
            local eq = line:find("=", 1, true)
            return line:sub(eq + 1)
        end
    end
end


local function findOperatingSystem(storage)
     mounts.mount("disk", storage, false)
     if mounts.exists("disk/boot/init.lua") then
         mounts.readFile("disk/boot/setting.txt") -- read the file somehow
         local fileString = [[
         osName = horizonOS
         osVersiom = 1.1.1
         ]]
         local osName = loadSettings(fileString)

         return setmetatable(
                 {
                         storageDevice = storage,
                         osName = osName,
                         __initCode = nil
                     },
                 operatingSystemEntry
                 )
     end
end

local function findOperatingSystems()
     local bootableMedia = biosApi.getBootableMedia()

     local foundOperatingSystems = {}
     if #bootableMedia ~= 0 then
         print(#bootableMedia, "storage disks found!")
         for i = 1, #bootableMedia do
             local os = findOperatingSystem(bootableMedia[i])
             if os ~= nil then
                 table.insert(foundOperatingSystems, os)
             end
         end
     end
     if #foundOperatingSystems then
         for i = 1, #foundOperatingSystems do
             print("osName ", foundOperatingSystems[i].osName)
         end
     end
end

