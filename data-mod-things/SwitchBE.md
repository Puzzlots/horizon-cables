# LaserPulser BlockEntity

This block assumes that your block model faces the diection `PosZ`

```python
'Directions' [
    NegX,
    PosX,
    NegY,
    PosY,
    NegZ,
    PosZ
]
```

<br><br>
BlockEntityID: `horizon:switch-block-entity` <br>

BlockEntityArgs: <br>

```java
// 'ports' takes in a list of directions to output power to
ports: []
```

BlockEntity Signals (`usable with the "base:block_entity_signal" block action`)
```java
"toggle" // toggles the block-entity's power state
```

BlockState Triggers (`can be called by the block-entity when avalible`)
```java
"onTurnOn" // called when the block-entity is turned on
"onTurnOff" // called when the block-entity is turned off
```