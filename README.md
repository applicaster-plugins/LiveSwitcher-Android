# Native Live Switcher

*Built by LATAM Team*

**Supports:** *iOS and Android*
*Current Version: 0.2.3 (Android), 0.0.15 (iOS)*

## About
The live switcher is a full screen plugin designed for video streaming purposes. It displays a set of channels or live events related to a specific context.

## What needs does it meet?
### Live Channels
By itself the live switcher can show the whole lineup of live channels available to the user. By having an inline player at the top of the list, a user can easily switch between streamings without going in and out of fullscreen video.

### Next Programs
Besides the live channels, the live switcher will show the next programs sorted by date and time. A reminder can be set for each next program.

### When to use?
- Several channels need to be displayed and make the user aware of the offer.
- Simulate the familiarity a user has with the easy switch between channels in a TV.
- A better experience making a combination of different elements (Video Player and reminders)

## Plugin configuration
Here you can find in detail manner the fields and data available for configuration.
| Key | Label | Type |
| --- | ----- | ---- |
| `ls_live_header_text` | Header text for live section | text input |
| `ls_live_header_text_color` | Text color for the live header | color picker |
| `ls_live_header_fontsize` | Font size of the live header text | text input |
| `ls_live_header_height` | Height of the live header | text input |
| `ls_live_header_background_color` | Background of the live header | color picker |
| `ls_next_header_text` | Header text for next section | text input |
| `ls_next_header_text_color` | Text color for the next header | text input |
| `ls_next_header_fontsize` | Font size of the next header text | text input |
| `ls_next_header_height` | Height of the next header | text input |
| `ls_next_header_background_color` | Background of the next header | color picker |
| `ls_program_title_text_color` | Text color for the title of the program | color picker |
| `ls_program_title_fontsize` | Font size of the title of the program | text input |
| `ls_program_schedule_text_color` | Text color for the schedule of the program | color picker |
| `ls_program_schedule_fontsize` | Font size of the schedule of the program | text input |
| `ls_watching_tag_text` | Text for the watching tag | text input |
| `ls_watching_tag_text_color` | Text color for the watching tag | color picker |
| `ls_watching_tag_fontsize` | Font size of the watching tag text | text input |
| `ls_watching_tag_text_background_color` | Watching tag text background color | color picker |
| `ls_live_event_tag_text` | Live event tag text | text input |
| `ls_live_event_tag_text_color` | Live event tag text color | color picker |
| `ls_live_event_tag_fontsize` | Live event tag text font size | text input |
| `ls_live_event_tag_text_background_color` | Live event tag text background color | color picker |
| `ls_channel_icon_height` | Channel icon height | text input |
| `ls_channel_icon_width` | Channel icon width | text input |
| `ls_background_color` | Background color of the list | color picker |
| `ls_set_reminder_text` | Text message showed to the user when actives a reminder. | text input |
| `ls_remove_reminder_text` | Text message showed to the user when removes a reminder. | text input |

### How to configure the plugin
In Zapp Builder, go to the screen plugin, and in the right panel, you will see the fields to be filled.

![Live switcher configuration](media/config.png)