* Status effect stacks now shows a block of the effect's color next to the color hex
* The info will show `The effect appears to have no descriptions...` if no translation key is present for the effect's description
* The info now accepts both `effect.[mod_id].[effect_name].description` and `effect.[mod_id].[effect_name].desc` for the description's translation key
* Users can now append custom stacks upon specific effects
  * They are defined in JSON files loaded from the path `assets/[mod_id]/emiffect/extra_stacks` (including sub-paths)
  * The basic structure is described in the [wiki](https://github.com/Prismwork/EMIffect/wiki/Append-custom-stacks-for-specific-effects)
* Fixes the info height when no stack is present
* (Publish) Fix versions not including platforms