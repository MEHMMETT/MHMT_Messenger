package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class EmojiItem(val char: String, val category: String, val keywords: String)

@Composable
fun EmojiPickerPanel(
    onEmojiSelected: (String) -> Unit,
    onBackspace: () -> Unit,
    onClose: () -> Unit,
    isDarkTheme: Boolean,
    language: String,
    emojiFontFamily: FontFamily?,
    modifier: Modifier = Modifier
) {
    // Extensive high-quality iOS/Android default emoji dataset
    val emojis = remember {
        listOf(
            // Smileys & Emotions
            EmojiItem("😀", "SMILEYS", "smile happy face grin xoshhal"),
            EmojiItem("😃", "SMILEYS", "smile happy face grin xoshhal"),
            EmojiItem("😄", "SMILEYS", "smile happy face grin xoshhal"),
            EmojiItem("😁", "SMILEYS", "smile happy beam xoshhal"),
            EmojiItem("😆", "SMILEYS", "laugh grin happy xoshhal"),
            EmojiItem("😅", "SMILEYS", "sweat laugh happy xoshhal"),
            EmojiItem("😂", "SMILEYS", "cry laugh tear happy xoshhal khande"),
            EmojiItem("🤣", "SMILEYS", "rofl laugh roll floor happy khande"),
            EmojiItem("🥲", "SMILEYS", "tear smile happy cry sad xoshhal"),
            EmojiItem("😊", "SMILEYS", "blush smile happy warm xoshhal"),
            EmojiItem("😇", "SMILEYS", "angel halo innocent xoshhal"),
            EmojiItem("🙂", "SMILEYS", "slight smile warm xoshhal"),
            EmojiItem("🙃", "SMILEYS", "upside down silly"),
            EmojiItem("😉", "SMILEYS", "wink eye xoshhal"),
            EmojiItem("😌", "SMILEYS", "relieved calm peace"),
            EmojiItem("😍", "SMILEYS", "heart eyes love xoshhal ashegh"),
            EmojiItem("🥰", "SMILEYS", "hearts love warm face ashegh"),
            EmojiItem("😘", "SMILEYS", "kiss heart blow love ashegh bos"),
            EmojiItem("😗", "SMILEYS", "kiss face"),
            EmojiItem("😙", "SMILEYS", "kiss smiling eyes"),
            EmojiItem("😚", "SMILEYS", "kiss closed eyes"),
            EmojiItem("😋", "SMILEYS", "yum delicious food hungry"),
            EmojiItem("😛", "SMILEYS", "tongue out play"),
            EmojiItem("😜", "SMILEYS", "wink tongue crazy play"),
            EmojiItem("🤪", "SMILEYS", "crazy zany eye play"),
            EmojiItem("😝", "SMILEYS", "tongue closed squint play"),
            EmojiItem("🤑", "SMILEYS", "money rich tongue gold pool"),
            EmojiItem("🤗", "SMILEYS", "hug warm open hands"),
            EmojiItem("🤔", "SMILEYS", "think wonder question"),
            EmojiItem("🫣", "SMILEYS", "peeking eye hide scare"),
            EmojiItem("🤫", "SMILEYS", "shh quiet silence"),
            EmojiItem("🫠", "SMILEYS", "melt hot disappear"),
            EmojiItem("🫡", "SMILEYS", "salute respect soldier"),
            EmojiItem("🥱", "SMILEYS", "yawn tired sleep"),
            EmojiItem("😴", "SMILEYS", "sleep tired snore khab"),
            EmojiItem("🤤", "SMILEYS", "drool sleep hungry"),
            EmojiItem("😪", "SMILEYS", "sleepy sad tear"),
            EmojiItem("😵", "SMILEYS", "dizzy dead"),
            EmojiItem("😵‍💫", "SMILEYS", "dizzy spiral eyes"),
            EmojiItem("🤐", "SMILEYS", "zipper mouth quiet"),
            EmojiItem("🥴", "SMILEYS", "woozy drunk"),
            EmojiItem("🤢", "SMILEYS", "nausea sick green"),
            EmojiItem("🤮", "SMILEYS", "puke vomit sick"),
            EmojiItem("🤧", "SMILEYS", "sneeze nose tissue sick"),
            EmojiItem("😷", "SMILEYS", "mask doctor sick"),
            EmojiItem("🤒", "SMILEYS", "thermometer sick fever"),
            EmojiItem("🤕", "SMILEYS", "bandage head hurt"),
            EmojiItem("🤠", "SMILEYS", "cowboy hat"),
            EmojiItem("🥳", "SMILEYS", "party celebrate hat balloon tavalod"),
            EmojiItem("😎", "SMILEYS", "cool glasses sun xashn"),
            EmojiItem("🤓", "SMILEYS", "nerd glasses smart"),
            EmojiItem("🧐", "SMILEYS", "monocle classy inspect"),
            EmojiItem("🫤", "SMILEYS", "confused unimpressed unsure"),
            EmojiItem("😟", "SMILEYS", "worried sad"),
            EmojiItem("🙁", "SMILEYS", "frown sad"),
            EmojiItem("☹️", "SMILEYS", "frown heavy sad"),
            EmojiItem("😮", "SMILEYS", "surprise mouth open"),
            EmojiItem("😯", "SMILEYS", "hushed surprise"),
            EmojiItem("😲", "SMILEYS", "astonished shock"),
            EmojiItem("😳", "SMILEYS", "flushed blush shock red"),
            EmojiItem("🥺", "SMILEYS", "pleading beg cry sad tear"),
            EmojiItem("😭", "SMILEYS", "cry sob loud sad tear gerye"),
            EmojiItem("😢", "SMILEYS", "sad cry tear gerye"),
            EmojiItem("😓", "SMILEYS", "sweat cold sad worry"),
            EmojiItem("😥", "SMILEYS", "sad sweat relieved"),
            EmojiItem("😰", "SMILEYS", "fear sweat blue"),
            EmojiItem("😨", "SMILEYS", "fear scared"),
            EmojiItem("😱", "SMILEYS", "scream fear shock ghost"),
            EmojiItem("🥵", "SMILEYS", "hot red sweat summer"),
            EmojiItem("🥶", "SMILEYS", "cold blue freeze ice winter"),
            EmojiItem("😡", "SMILEYS", "pout angry mad red khashm"),
            EmojiItem("😠", "SMILEYS", "angry mad face khashm"),
            EmojiItem("🤬", "SMILEYS", "cursing swearing mad angry"),
            EmojiItem("🤯", "SMILEYS", "mind blown explode brain shock"),
            EmojiItem("👿", "SMILEYS", "devil angry purple"),
            EmojiItem("😈", "SMILEYS", "devil smile purple"),
            EmojiItem("💀", "SMILEYS", "skull bones dead marg"),
            EmojiItem("☠️", "SMILEYS", "crossbones skull poison danger"),
            EmojiItem("💩", "SMILEYS", "poop turd brown"),
            EmojiItem("🤡", "SMILEYS", "clown circus play jester"),
            EmojiItem("👻", "SMILEYS", "ghost spook spirit"),
            EmojiItem("👽", "SMILEYS", "alien space ufo"),
            EmojiItem("🤖", "SMILEYS", "robot tech mechanical"),
            
            // Hearts (EVERY single heart color)
            EmojiItem("❤️", "SMILEYS", "heart love red ghalb ashegh"),
            EmojiItem("🧡", "SMILEYS", "heart love orange ghalb"),
            EmojiItem("💛", "SMILEYS", "heart love yellow ghalb"),
            EmojiItem("💚", "SMILEYS", "heart love green ghalb"),
            EmojiItem("💙", "SMILEYS", "heart love blue ghalb"),
            EmojiItem("💜", "SMILEYS", "heart love purple ghalb"),
            EmojiItem("🖤", "SMILEYS", "heart love black ghalb"),
            EmojiItem("🤍", "SMILEYS", "heart love white ghalb"),
            EmojiItem("🤎", "SMILEYS", "heart love brown ghalb"),
            EmojiItem("💔", "SMILEYS", "heart broken love sad ghalb broken"),
            EmojiItem("❤️‍🔥", "SMILEYS", "heart fire love hot ghalb atash"),
            EmojiItem("❤️‍🩹", "SMILEYS", "heart bandage heal love"),
            EmojiItem("💖", "SMILEYS", "sparkle heart love ghalb drakhshan"),
            EmojiItem("💗", "SMILEYS", "growing heart love"),
            EmojiItem("💓", "SMILEYS", "beating heart love pulse"),
            EmojiItem("💞", "SMILEYS", "revolving hearts love"),
            EmojiItem("💕", "SMILEYS", "two hearts love"),
            EmojiItem("💟", "SMILEYS", "heart decoration purple"),
            EmojiItem("❣️", "SMILEYS", "heart exclamation love red"),
            EmojiItem("💘", "SMILEYS", "heart arrow cupids love"),
            EmojiItem("💝", "SMILEYS", "heart ribbon gift present"),
            EmojiItem("🔥", "SMILEYS", "fire hot match atash cansoz"),
            EmojiItem("✨", "SMILEYS", "sparkles shine bright stars"),
            EmojiItem("🌟", "SMILEYS", "star shining glow yellow"),
            EmojiItem("⭐", "SMILEYS", "star yellow setare"),
            
            // Gestures & Hands
            EmojiItem("👍", "SMILEYS", "thumbs up agree good safe liyk"),
            EmojiItem("👎", "SMILEYS", "thumbs down disagree bad"),
            EmojiItem("👏", "SMILEYS", "clap hands praise bafsh"),
            EmojiItem("🙌", "SMILEYS", "raise hands celebrate"),
            EmojiItem("👐", "SMILEYS", "open hands warm"),
            EmojiItem("🤲", "SMILEYS", "palms together pray dua"),
            EmojiItem("🤝", "SMILEYS", "handshake agree deal partnership"),
            EmojiItem("🙏", "SMILEYS", "pray please thank you namaste"),
            EmojiItem("👋", "SMILEYS", "wave hand hello goodbye"),
            EmojiItem("🤚", "SMILEYS", "raised back hand"),
            EmojiItem("🖐️", "SMILEYS", "hand fingers splayed"),
            EmojiItem("✋", "SMILEYS", "raised hand stop bokhosh"),
            EmojiItem("🖖", "SMILEYS", "vulcan salute spock"),
            EmojiItem("👌", "SMILEYS", "ok hand correct safe perfect"),
            EmojiItem("🤌", "SMILEYS", "pinched fingers wait farsi chakeri"),
            EmojiItem("🤏", "SMILEYS", "pinched hand small little"),
            EmojiItem("✌️", "SMILEYS", "victory peace hand pyroozi"),
            EmojiItem("🤞", "SMILEYS", "fingers crossed luck hope"),
            EmojiItem("🫰", "SMILEYS", "hand finger heart love korean"),
            EmojiItem("🤟", "SMILEYS", "love you gesture"),
            EmojiItem("🤘", "SMILEYS", "rock on devil horns metal"),
            EmojiItem("🤙", "SMILEYS", "call me hand phone"),
            EmojiItem("👈", "SMILEYS", "point left backhand"),
            EmojiItem("👉", "SMILEYS", "point right backhand"),
            EmojiItem("👆", "SMILEYS", "point up backhand"),
            EmojiItem("🖕", "SMILEYS", "middle finger rude gesture"),
            EmojiItem("👇", "SMILEYS", "point down backhand"),
            EmojiItem("☝️", "SMILEYS", "point up index finger"),
            EmojiItem("✊", "SMILEYS", "fist raised power"),
            EmojiItem("👊", "SMILEYS", "fist oncoming punch strike"),
            EmojiItem("🤛", "SMILEYS", "fist left"),
            EmojiItem("🤜", "SMILEYS", "fist right"),
            EmojiItem("✍️", "SMILEYS", "writing hand pen"),
            EmojiItem("💅", "SMILEYS", "nail polish cosmetic polish"),
            EmojiItem("🤳", "SMILEYS", "selfie camera phone photo"),
            EmojiItem("💪", "SMILEYS", "biceps muscle strong power"),
            EmojiItem("🦾", "SMILEYS", "mechanical arm robotic strong"),
            EmojiItem("💋", "SMILEYS", "kiss mark lipstick red redlips"),
            
            // Animals & Nature
            EmojiItem("🐶", "ANIMALS", "dog puppy bark sag"),
            EmojiItem("🐱", "ANIMALS", "cat kitty meow gorbe"),
            EmojiItem("🐭", "ANIMALS", "mouse moosh"),
            EmojiItem("🐹", "ANIMALS", "hamster"),
            EmojiItem("🐰", "ANIMALS", "rabbit bunny khargosh"),
            EmojiItem("🦊", "ANIMALS", "fox rubah"),
            EmojiItem("🐻", "ANIMALS", "bear khers"),
            EmojiItem("🐼", "ANIMALS", "panda cute"),
            EmojiItem("🐨", "ANIMALS", "koala"),
            EmojiItem("🐯", "ANIMALS", "tiger wild babr"),
            EmojiItem("🦁", "ANIMALS", "lion wild king shir"),
            EmojiItem("🐮", "ANIMALS", "cow gav"),
            EmojiItem("🐷", "ANIMALS", "pig khook"),
            EmojiItem("🐽", "ANIMALS", "pig nose snout"),
            EmojiItem("🐸", "ANIMALS", "frog ghorbaghe"),
            EmojiItem("🐵", "ANIMALS", "monkey meymoon"),
            EmojiItem("🙈", "ANIMALS", "monkey see no evil hide"),
            EmojiItem("🙉", "ANIMALS", "monkey hear no evil"),
            EmojiItem("🙊", "ANIMALS", "monkey speak no evil"),
            EmojiItem("🐒", "ANIMALS", "monkey climb meymoon"),
            EmojiItem("🐔", "ANIMALS", "chicken hen morgh"),
            EmojiItem("🐧", "ANIMALS", "penguin"),
            EmojiItem("🐦", "ANIMALS", "bird parande"),
            EmojiItem("🐤", "ANIMALS", "chick bird jooje"),
            EmojiItem("🐣", "ANIMALS", "hatching chick bird"),
            EmojiItem("🐥", "ANIMALS", "chick front bird jooje"),
            EmojiItem("🦅", "ANIMALS", "eagle bird hunting"),
            EmojiItem("🦉", "ANIMALS", "owl joghd"),
            EmojiItem("🦇", "ANIMALS", "bat fly night"),
            EmojiItem("🐺", "ANIMALS", "wolf wild gorg"),
            EmojiItem("🐗", "ANIMALS", "boar wild pig"),
            EmojiItem("🐴", "ANIMALS", "horse asb"),
            EmojiItem("🦄", "ANIMALS", "unicorn magic"),
            EmojiItem("🐝", "ANIMALS", "bee honey insect zanboor"),
            EmojiItem("🐛", "ANIMALS", "bug caterpillar insect"),
            EmojiItem("🦋", "ANIMALS", "butterfly insect parvane"),
            EmojiItem("🐌", "ANIMALS", "snail insect slowly"),
            EmojiItem("🐞", "ANIMALS", "ladybug ladybeetle insect"),
            EmojiItem("🐜", "ANIMALS", "ant insect"),
            EmojiItem("🕷️", "ANIMALS", "spider insect ankabout"),
            EmojiItem("🐍", "ANIMALS", "snake mar"),
            EmojiItem("🐢", "ANIMALS", "turtle lakposht"),
            EmojiItem("🐙", "ANIMALS", "octopus marine"),
            EmojiItem("🦑", "ANIMALS", "squid marine"),
            EmojiItem("🐬", "ANIMALS", "dolphin dolphin"),
            EmojiItem("🐳", "ANIMALS", "whale spouting"),
            EmojiItem("🐋", "ANIMALS", "whale marine"),
            EmojiItem("🦈", "ANIMALS", "shark fish wild"),
            EmojiItem("🐊", "ANIMALS", "crocodile reptile tame"),
            EmojiItem("🐆", "ANIMALS", "leopard wild"),
            EmojiItem("🦓", "ANIMALS", "zebra horse striped"),
            EmojiItem("🦍", "ANIMALS", "gorilla monkey wild"),
            EmojiItem("🐘", "ANIMALS", "elephant elephant feel"),
            EmojiItem("🦛", "ANIMALS", "hippo hippo"),
            EmojiItem("🐪", "ANIMALS", "camel desert"),
            EmojiItem("🦒", "ANIMALS", "giraffe tall animal"),
            EmojiItem("🦘", "ANIMALS", "kangaroo pouch jump"),
            EmojiItem("🐿️", "ANIMALS", "squirrel chipmunk nut"),
            EmojiItem("🦔", "ANIMALS", "hedgehog spike prickle"),
            EmojiItem("🌹", "ANIMALS", "rose flower red gol"),
            EmojiItem("🌸", "ANIMALS", "cherry blossom flower gol"),
            EmojiItem("🌷", "ANIMALS", "tulip flower gol"),
            EmojiItem("🌻", "ANIMALS", "sunflower yellow flower"),
            EmojiItem("🌼", "ANIMALS", "blossom yellow flower"),
            EmojiItem("💐", "ANIMALS", "bouquet bunch of flowers"),
            EmojiItem("🥀", "ANIMALS", "wilted flower dead gol"),
            EmojiItem("🌲", "ANIMALS", "tree forest wood derakht"),
            EmojiItem("🌳", "ANIMALS", "deciduous tree forest"),
            EmojiItem("🌴", "ANIMALS", "palm tree beach tropical"),
            EmojiItem("🌵", "ANIMALS", "cactus desert dry"),
            EmojiItem("☘️", "ANIMALS", "shamrock green leaf"),
            EmojiItem("🍀", "ANIMALS", "clover green leaf luck"),
            EmojiItem("🍁", "ANIMALS", "maple leaf autumn barg"),
            EmojiItem("🍂", "ANIMALS", "fallen leaf autumn barg"),
            EmojiItem("🍃", "ANIMALS", "leaf fluttering green"),
            EmojiItem("🍄", "ANIMALS", "mushroom fungus"),
            EmojiItem("☀️", "ANIMALS", "sun solar day bright aftab"),
            EmojiItem("🌤️", "ANIMALS", "sun behind small cloud"),
            EmojiItem("☁️", "ANIMALS", "cloud overcast abr"),
            EmojiItem("🌧️", "ANIMALS", "cloud with rain baran"),
            EmojiItem("⛈️", "ANIMALS", "cloud with lightning rain storm"),
            EmojiItem("❄️", "ANIMALS", "snowflake winter cold barf"),
            EmojiItem("🌊", "ANIMALS", "wave water sea ocean darya"),
            
            // Food & Drink
            EmojiItem("🍏", "FOOD", "apple green sib"),
            EmojiItem("🍎", "FOOD", "apple red sib"),
            EmojiItem("🍐", "FOOD", "pear"),
            EmojiItem("🍊", "FOOD", "orange portaghal"),
            EmojiItem("🍋", "FOOD", "lemon limoo"),
            EmojiItem("🍌", "FOOD", "banana moz"),
            EmojiItem("🍉", "FOOD", "watermelon hendevane"),
            EmojiItem("🍇", "FOOD", "grapes fruit angoor"),
            EmojiItem("🍓", "FOOD", "strawberry tootfarangi"),
            EmojiItem("🫐", "FOOD", "blueberries berry fruit"),
            EmojiItem("🍈", "FOOD", "melon fruit"),
            EmojiItem("🍒", "FOOD", "cherry gilas"),
            EmojiItem("🍑", "FOOD", "peach holoo"),
            EmojiItem("🥭", "FOOD", "mango anbeh"),
            EmojiItem("🍍", "FOOD", "pineapple ananas"),
            EmojiItem("🥥", "FOOD", "coconut narghil"),
            EmojiItem("🥝", "FOOD", "kiwi fruit"),
            EmojiItem("🍅", "FOOD", "tomato goje"),
            EmojiItem("🍆", "FOOD", "eggplant bademjan"),
            EmojiItem("🥑", "FOOD", "avocado"),
            EmojiItem("🥦", "FOOD", "broccoli green"),
            EmojiItem("🥒", "FOOD", "cucumber khiar"),
            EmojiItem("🌶️", "FOOD", "chili pepper spicy goje"),
            EmojiItem("🧅", "FOOD", "onion piyaz"),
            EmojiItem("🥔", "FOOD", "potato sibzamini"),
            EmojiItem("🥕", "FOOD", "carrot havij"),
            EmojiItem("🌽", "FOOD", "corn maize"),
            EmojiItem("🍞", "FOOD", "bread loaf nan"),
            EmojiItem("🥐", "FOOD", "croissant nan"),
            EmojiItem("🥯", "FOOD", "bagel bread"),
            EmojiItem("🥖", "FOOD", "baguette bread"),
            EmojiItem("🧀", "FOOD", "cheese panel"),
            EmojiItem("🍳", "FOOD", "egg fry pan tokham"),
            EmojiItem("🥓", "FOOD", "bacon pork"),
            EmojiItem("🥩", "FOOD", "steak meat goosht"),
            EmojiItem("🍗", "FOOD", "chicken leg thigh goosht"),
            EmojiItem("🍖", "FOOD", "meat on bone goosht"),
            EmojiItem("🍕", "FOOD", "pizza cheese slice junk food"),
            EmojiItem("🍔", "FOOD", "burger fast food sandwich hambur"),
            EmojiItem("🍟", "FOOD", "fries fast food potato sibzamini"),
            EmojiItem("🌭", "FOOD", "hotdog fast food sandwich"),
            EmojiItem("🥪", "FOOD", "sandwich lunch food"),
            EmojiItem("🌮", "FOOD", "taco mexican"),
            EmojiItem("🌯", "FOOD", "burrito mexican wrap"),
            EmojiItem("🍿", "FOOD", "popcorn snack pofila"),
            EmojiItem("🥫", "FOOD", "canned food tomato"),
            EmojiItem("🍱", "FOOD", "bento box japanese"),
            EmojiItem("🍚", "FOOD", "rice bowl cholo"),
            EmojiItem("🍛", "FOOD", "curry rice food"),
            EmojiItem("🍜", "FOOD", "ramen noodles soup"),
            EmojiItem("🍝", "FOOD", "pasta spaghetti noodles"),
            EmojiItem("🍣", "FOOD", "sushi japanese"),
            EmojiItem("🥟", "FOOD", "dumpling chinese food"),
            EmojiItem("🍦", "FOOD", "ice cream dessert sweet bastani"),
            EmojiItem("🍧", "FOOD", "shaved ice sweet"),
            EmojiItem("🍨", "FOOD", "ice cream bowl sweet bastani"),
            EmojiItem("🍩", "FOOD", "donut dessert sweet shini"),
            EmojiItem("🍪", "FOOD", "cookie biscuit dessert sweet kuluche"),
            EmojiItem("🎂", "FOOD", "cake birthday celebrate sweet keyk"),
            EmojiItem("🍰", "FOOD", "cake slice sweet keyk"),
            EmojiItem("🧁", "FOOD", "cupcake sweet cake keyk"),
            EmojiItem("🥧", "FOOD", "pie pastry dessert"),
            EmojiItem("🍫", "FOOD", "chocolate shokolat sweet"),
            EmojiItem("🍬", "FOOD", "candy sweet dande"),
            EmojiItem("🍭", "FOOD", "lollipop sweet candy"),
            EmojiItem("🍮", "FOOD", "custard pudding sweet"),
            EmojiItem("🍯", "FOOD", "honey pot sweet asal"),
            EmojiItem("🥛", "FOOD", "milk glass drink sheer"),
            EmojiItem("☕", "FOOD", "coffee tea cup hot cafe ghahve"),
            EmojiItem("🫖", "FOOD", "teapot tea chaye"),
            EmojiItem("🍵", "FOOD", "tea cup green chaye"),
            EmojiItem("🍾", "FOOD", "champagne bottle pop celebration"),
            EmojiItem("🍷", "FOOD", "wine drink alcohol glass"),
            EmojiItem("🍸", "FOOD", "cocktail glass drink alcohol"),
            EmojiItem("🍹", "FOOD", "tropical drink cocktail"),
            EmojiItem("🍺", "FOOD", "beer drink alcohol abjoo"),
            EmojiItem("🍻", "FOOD", "beers drink toast cheers abjoo"),
            EmojiItem("🥂", "FOOD", "clinking glasses toast cheers"),
            EmojiItem("🥃", "FOOD", "whiskey glass drink liquor"),
            EmojiItem("🥤", "FOOD", "soda cup straw soft drink"),
            EmojiItem("🧋", "FOOD", "boba bubble milk tea"),
            EmojiItem("🧃", "FOOD", "juice box drink"),
            EmojiItem("🧊", "FOOD", "ice cube freeze yakh"),
            
            // Activities
            EmojiItem("⚽", "ACTIVITIES", "soccer football ball match football"),
            EmojiItem("🏀", "ACTIVITIES", "basketball ball"),
            EmojiItem("🏈", "ACTIVITIES", "football ball"),
            EmojiItem("⚾", "ACTIVITIES", "baseball ball"),
            EmojiItem("🥎", "ACTIVITIES", "softball ball"),
            EmojiItem("🎾", "ACTIVITIES", "tennis ball sport"),
            EmojiItem("🏐", "ACTIVITIES", "volleyball ball sport"),
            EmojiItem("🏉", "ACTIVITIES", "rugby football ball"),
            EmojiItem("🎱", "ACTIVITIES", "billiards pool ball 8ball"),
            EmojiItem("🏓", "ACTIVITIES", "ping pong table tennis"),
            EmojiItem("🏸", "ACTIVITIES", "badminton racket birdie"),
            EmojiItem("🥊", "ACTIVITIES", "boxing glove fight hand"),
            EmojiItem("🥋", "ACTIVITIES", "karate judo martial arts"),
            EmojiItem("🛹", "ACTIVITIES", "skateboard skate board"),
            EmojiItem("🛼", "ACTIVITIES", "roller skate shoes"),
            EmojiItem("🎳", "ACTIVITIES", "bowling pins ball"),
            EmojiItem("🎯", "ACTIVITIES", "dart bullseye hit target"),
            EmojiItem("♟️", "ACTIVITIES", "chess pawn boardgame shatranj"),
            EmojiItem("🧩", "ACTIVITIES", "puzzle piece game puzzle"),
            EmojiItem("🎮", "ACTIVITIES", "game video controller play bazi"),
            EmojiItem("🏆", "ACTIVITIES", "trophy win gold champion jashn"),
            EmojiItem("🥇", "ACTIVITIES", "first medal gold champion medp"),
            EmojiItem("🥈", "ACTIVITIES", "second medal silver"),
            EmojiItem("🥉", "ACTIVITIES", "third medal bronze"),
            EmojiItem("🎸", "ACTIVITIES", "guitar music instrument"),
            EmojiItem("🎹", "ACTIVITIES", "piano music instrument keyboard"),
            EmojiItem("🎧", "ACTIVITIES", "headphones music listen sound"),
            EmojiItem("🎤", "ACTIVITIES", "mic microphone sing sound"),
            EmojiItem("🥁", "ACTIVITIES", "drum music instrument beat"),
            EmojiItem("🎷", "ACTIVITIES", "saxophone music wind instrument"),
            EmojiItem("🎺", "ACTIVITIES", "trumpet brass instrument music"),
            EmojiItem("🎻", "ACTIVITIES", "violin string instrument music"),
            EmojiItem("🎬", "ACTIVITIES", "clapper film movie cinema"),
            EmojiItem("🎨", "ACTIVITIES", "art paint brush palette naghashi"),
            EmojiItem("🎟️", "ACTIVITIES", "ticket theater movie"),
            EmojiItem("🎪", "ACTIVITIES", "circus tent show"),
            
            // Travel
            EmojiItem("🚗", "TRAVEL", "car drive vehicle travel mashin"),
            EmojiItem("🚕", "TRAVEL", "taxi cab mashin travel"),
            EmojiItem("🚙", "TRAVEL", "suv car drive mashin"),
            EmojiItem("🚌", "TRAVEL", "bus vehicle transport"),
            EmojiItem("🏍️", "TRAVEL", "motorcycle motor bike"),
            EmojiItem("🚲", "TRAVEL", "bicycle bike docharkhe"),
            EmojiItem("🚃", "TRAVEL", "train railway vehicle ghadar"),
            EmojiItem("🚇", "TRAVEL", "subway metro transport"),
            EmojiItem("✈️", "TRAVEL", "plane flight travel sky havapeyma"),
            EmojiItem("🚀", "TRAVEL", "rocket space fly travel"),
            EmojiItem("🛸", "TRAVEL", "ufo alien space flying saucer"),
            EmojiItem("🚁", "TRAVEL", "helicopter copter chopper"),
            EmojiItem("🚢", "TRAVEL", "ship boat marine kashti"),
            EmojiItem("⚓", "TRAVEL", "anchor boat ship kashti"),
            EmojiItem("🚓", "TRAVEL", "police car police mashin"),
            EmojiItem("🚑", "TRAVEL", "ambulance medical car"),
            EmojiItem("🚒", "TRAVEL", "fire engine truck car"),
            EmojiItem("🚜", "TRAVEL", "tractor farm vehicle"),
            EmojiItem("🏠", "TRAVEL", "house home building khane"),
            EmojiItem("🏡", "TRAVEL", "house garden home khane"),
            EmojiItem("🏢", "TRAVEL", "office building work edare"),
            EmojiItem("🏥", "TRAVEL", "hospital medical bimarestan"),
            EmojiItem("🏦", "TRAVEL", "bank finance money"),
            EmojiItem("🏨", "TRAVEL", "hotel building vacation"),
            EmojiItem("🏫", "TRAVEL", "school education dars madrese"),
            EmojiItem("🏭", "TRAVEL", "factory industry building"),
            EmojiItem("🏰", "TRAVEL", "castle fortress building ghal"),
            EmojiItem("🗼", "TRAVEL", "tokyo eiffel tower"),
            EmojiItem("🗽", "TRAVEL", "statue liberty"),
            EmojiItem("⛪", "TRAVEL", "church building"),
            EmojiItem("🕌", "TRAVEL", "mosque building islam masjed"),
            EmojiItem("⛺", "TRAVEL", "tent camping outdoor"),
            EmojiItem("⛲", "TRAVEL", "fountain park water"),
            EmojiItem("🏔️", "TRAVEL", "mountain snow cold kooh"),
            EmojiItem("⛰️", "TRAVEL", "mountain volcano lava kooh"),
            EmojiItem("🏖️", "TRAVEL", "beach umbrella vacation darya"),
            EmojiItem("🏜️", "TRAVEL", "desert sand dry kavir"),
            EmojiItem("🏝️", "TRAVEL", "desert island beach tropical"),
            
            // Objects
            EmojiItem("💡", "OBJECTS", "bulb light idea lamoo"),
            EmojiItem("💻", "OBJECTS", "laptop computer coder pc tech"),
            EmojiItem("📱", "OBJECTS", "phone screen mobile tech gooshi"),
            EmojiItem("⌚", "OBJECTS", "watch time clock saat"),
            EmojiItem("⏰", "OBJECTS", "alarm clock wake saat"),
            EmojiItem("⏳", "OBJECTS", "hourglass time clock"),
            EmojiItem("💵", "OBJECTS", "dollar money cash pool"),
            EmojiItem("💶", "OBJECTS", "euro money cash pool"),
            EmojiItem("💷", "OBJECTS", "pound money cash pool"),
            EmojiItem("🪙", "OBJECTS", "coin gold money silver pool"),
            EmojiItem("🔑", "OBJECTS", "key open lock کلید"),
            EmojiItem("🔒", "OBJECTS", "lock closed قفل"),
            EmojiItem("🔓", "OBJECTS", "lock open"),
            EmojiItem("🔨", "OBJECTS", "hammer tool build chakoosh"),
            EmojiItem("🪓", "OBJECTS", "axe tool wood tabar"),
            EmojiItem("🔧", "OBJECTS", "wrench tool fix spanner"),
            EmojiItem("🪛", "OBJECTS", "screwdriver tool fix"),
            EmojiItem("⚙️", "OBJECTS", "gear cog wheel mechanical charkh"),
            EmojiItem("🛡️", "OBJECTS", "shield armor protect war"),
            EmojiItem("⚔️", "OBJECTS", "swords weapon war fight shamshir"),
            EmojiItem("🔫", "OBJECTS", "pistol gun water weapon"),
            EmojiItem("💣", "OBJECTS", "bomb explode weapon jashn"),
            EmojiItem("💉", "OBJECTS", "syringe medical injection ampool"),
            EmojiItem("💊", "OBJECTS", "pill capsule medicine ghors"),
            EmojiItem("🩹", "OBJECTS", "adhesive bandage medical wound"),
            EmojiItem("🔬", "OBJECTS", "microscope science lab"),
            EmojiItem("🔭", "OBJECTS", "telescope science space stars"),
            EmojiItem("✉️", "OBJECTS", "envelope mail letter name"),
            EmojiItem("📥", "OBJECTS", "inbox tray mail receive"),
            EmojiItem("📤", "OBJECTS", "outbox tray mail send"),
            EmojiItem("📦", "OBJECTS", "package box delivery parcel"),
            EmojiItem("🎁", "OBJECTS", "gift present box kado tavalod"),
            EmojiItem("🎈", "OBJECTS", "balloon party badkonak"),
            EmojiItem("🎉", "OBJECTS", "party popper celebrate jashn"),
            EmojiItem("🎊", "OBJECTS", "ball confetti celebrate"),
            EmojiItem("📷", "OBJECTS", "camera photo akasi doorbin"),
            EmojiItem("🔍", "OBJECTS", "magnifier search jostejo"),
            EmojiItem("📕", "OBJECTS", "book red ketab"),
            EmojiItem("✏️", "OBJECTS", "pencil write medad"),
            EmojiItem("💼", "OBJECTS", "briefcase work kif"),
            EmojiItem("🪞", "OBJECTS", "mirror reflection glass ayene"),
            EmojiItem("⚰️", "OBJECTS", "coffin death funeral"),
            EmojiItem("🚬", "OBJECTS", "cigarette smoke tabaco")
        )
    }

    var selectedTab by remember { mutableStateOf("SMILEYS") }
    var searchQuery by remember { mutableStateOf("") }
    var mainTabSelected by remember { mutableStateOf("EMOJI") } // "EMOJI", "GIF", "STICKER"

    val filteredEmojis = remember(selectedTab, searchQuery) {
        if (searchQuery.isNotBlank()) {
            emojis.filter {
                it.char.contains(searchQuery) || 
                it.keywords.contains(searchQuery, ignoreCase = true)
            }
        } else {
            emojis.filter { it.category == selectedTab }
        }
    }

    val surfaceColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val dividerColor = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .background(surfaceColor)
    ) {
        // Top boundary divider
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(dividerColor))

        if (mainTabSelected == "EMOJI") {
            // Category Selector Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categories = listOf(
                    Triple("SMILEYS", Icons.Default.SentimentSatisfied, if (language == "fa") "شکلک‌ها" else "Smileys"),
                    Triple("ANIMALS", Icons.Default.Pets, if (language == "fa") "حیوانات" else "Animals"),
                    Triple("FOOD", Icons.Default.Restaurant, if (language == "fa") "خوراکی‌ها" else "Food"),
                    Triple("ACTIVITIES", Icons.Default.SportsBasketball, if (language == "fa") "ورزش" else "Activities"),
                    Triple("TRAVEL", Icons.Default.DirectionsCar, if (language == "fa") "سفر" else "Travel"),
                    Triple("OBJECTS", Icons.Default.Lightbulb, if (language == "fa") "اشیاء" else "Objects")
                )

                categories.forEach { (cat, icon, title) ->
                    val isSelected = selectedTab == cat && searchQuery.isEmpty()
                    IconButton(
                        onClick = {
                            selectedTab = cat
                            searchQuery = ""
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (isSelected) textPrimary.copy(alpha = 0.12f) else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isSelected) (if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1E3A8A)) else textPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Search Bar for quick emoji access
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { 
                    Text(
                        text = if (language == "fa") "جستجوی ایموجی..." else "Search emojis...", 
                        fontSize = 12.sp,
                        color = textPrimary.copy(alpha = 0.4f)
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textPrimary.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = textPrimary.copy(alpha = 0.2f),
                    unfocusedBorderColor = textPrimary.copy(alpha = 0.08f),
                    focusedContainerColor = if (isDarkTheme) Color.Black.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.4f),
                    unfocusedContainerColor = if (isDarkTheme) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .height(40.dp),
                textStyle = TextStyle(fontSize = 13.sp, color = textPrimary)
            )

            // Emojis Scrollable Grid
            if (filteredEmojis.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == "fa") "ایموجی یافت نشد" else "No emojis found",
                        color = textPrimary.copy(alpha = 0.4f),
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(filteredEmojis) { item ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .clickable { onEmojiSelected(item.char) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.char,
                                fontSize = 26.sp,
                                fontFamily = emojiFontFamily,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else if (mainTabSelected == "GIF") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gif,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = textPrimary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == "fa") "قابلیت ارسال گیف به زودی!" else "GIF support is coming soon!",
                    color = textPrimary.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = textPrimary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == "fa") "استیکرها به زودی فعال می‌شوند!" else "Stickers are coming soon!",
                    color = textPrimary.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Navigation Bar
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(dividerColor))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(44.dp)
                .background(if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFE2E8F0)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // normal soft keyboard toggle
            TextButton(
                onClick = onClose,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = "ABC",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1E3A8A)
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Tab toggles: Emoji, GIFs, Stickers
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val mainTabs = listOf(
                    Pair("EMOJI", if (language == "fa") "ایموجی" else "Emoji"),
                    Pair("GIF", if (language == "fa") "گیف" else "GIFs"),
                    Pair("STICKER", if (language == "fa") "استیکر" else "Stickers")
                )

                mainTabs.forEach { (tab, label) ->
                    val isSelected = mainTabSelected == tab
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) textPrimary.copy(alpha = 0.12f) else Color.Transparent
                            )
                            .clickable { mainTabSelected = tab }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) textPrimary else textPrimary.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Delete Backspace button
            IconButton(
                onClick = onBackspace,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = textPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
