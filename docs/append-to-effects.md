> [!IMPORTANT]
> The features described in this page are only available since 2.1.0.

EMIffect provides a way for users and modders to append custom stacks for specific effects to fix the gaps where EMIffect cannot take all these compatibility issues into consideration.

Currently, the mod appends these stacks by parsing JSON files placed under `assets/[mod_id]/emiffect/extra_stacks` in **resource packs**. The basic structure of these files is pretty simple:
```jsonc
{
  "id": "<effect_id>",
  "stacks": [
    // Here are the stacks to be appended
    // Multiple stacks can be put here
  ]
}
```
The `id` field is the effect's **full** ID (with a namespace and a path). It has nothing to do with the file name; if multiple files with the same `id` field are present, they will all be applied.

The `stacks` field is an array of stacks to be appended. Every element in this array is either a string or an object describing a stack, and the JSON structure of these stacks is specified in [this page in EMI's wiki](https://github.com/emilyploszaj/emi/wiki/Customization-Guide).

For example, if we want to add the wither rose and the wither spawn egg (representing a wither) to the Wither effect's info, we can do this:
```json
{
  "id": "minecraft:wither",
  "stacks": [
    "item:minecraft:wither_spawn_egg",
    "item:minecraft:wither_rose"
  ]
}
```
This is also how EMIffect itself adds these stacks to the wither effect's info.
