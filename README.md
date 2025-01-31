# Color-Based Vignettes Minecraft
Many people struggle with making a color-based vignette in fabric minecraft. I also had this issue, The painful part is if you fall in the trap of using Minecraft Vanilla's Built-In Vignette Renderer, you will often be **frustrated** and at worst **burn out** from the project entirely. The problem arises when you want colors in it, the colors get sort of inverted and the code is hard to follow so often times you cannot find the reverse calculations to cancel out this inversion bug. I have struggled on this for about ``2 entire days``. However i managed to come up with a very elegant simple solution, that even a junior modder can kind of get a grip on what the code does. So what is this magical solution? Here are the steps provided:

The prerequesists are:
- Fabric is required
- Definetely Minecraft 1.21.3 (it may work on other versions or not, you can adjust the logic tho)

### 1. Use the image provided for the vignette texture<br>
![](https://media.discordapp.net/attachments/836331093531164752/1334955245100466269/vignette.png?ex=679e6974&is=679d17f4&hm=e6ac9e2c2ade53b9b9fe2732e3fc0ee015c49ba1c203cbea7bdaa33b138f803d&=&format=webp&quality=lossless&width=512&height=512)
<br>This image specifically is a mask. It is black and white so later on we can tint it any color our heart desires. This mask can be replaced with pretty much anything else, although ideally the center point needs to transition to transparent

### 2. Mixin into the render method in ``InGameHUD`` like so:
```java
@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHUDMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void myMod$drawVignette(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // The code goes here
    }
}
```
Now we make our mixin. Although you may ask, what is a mixin? In short it is a way for us the mod developers to modify vanilla's code and shape it in very creative ways. I highly reccomend checking it [kaupenjoe's video](https://www.youtube.com/watch?v=U7j4bl_UAII) on mixins as he is doing a good job explaining the theory behind it, even tho we are working on a much newer version than 1.18.1

### 3. Register your vignette like so(on the mixin perferably)
```java
@Unique
private static final Identifier MY_VIGNETTE = Identifier.of(
  MyMod.MOD_ID, "textures/vignette.png"
);
```
Inside the mixin, just before the method, we initialize the texture to use. MyMod.MOD_ID is your mod's ID and ``"textures/vignette.png"`` is the path for the vignette mask texture explained on the [first part](#1-use-the-image-provided-for-the-vignette-texture), we will use this texture later down the line in order to make this work

### 4. Have a color value like so, where ``INTENSITY`` is the alpha component and basically it fades the vignette
```java
color = ColorHelper.fromARGB(INTENSITY, r, g, b);
```
We set our color where the alpha channel is represented as ``INTENSITY`` and demonstrates the intensity of the vignette effect, lower values mean lower strength of the vignette and vice versa. Then we use RGB colors where r is the red color component, green is the green color component and finally blue is the blue color component. Under the hood this turns it into a integer in the fashion of ``0x123456`` where its a hexadecimal number. You can also do this yourself but its more convinient to do this instead
### 5. Copy paste this code to draw the texture on the inject method mixin:
```java
context.drawTexture(
  RenderLayer::getGuiTexturedOverlay,
  MY_VIGNETTE,
  0,
  0,
  0.0f,
  0.0f,
  context.getScaledWindowWidth(),
  context.getScaledWindowHeight(),
  context.getScaledWindowWidth(),
  context.getScaledWindowHeight(),
  color
);
```
We provide this code within the method ``myMod$drawVignette``, where we put our vignette texture on the top left and stretch it to fit the entire screen. Instead of using ``RenderLayer::getVignette`` as you probably intended todo, we use ``  RenderLayer::getGuiTexturedOverlay``, the reason why is explained below in more detail

Now with that said your code should look like this:
```java
@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHUDMixin {
    @Unique
    private static final Identifier MY_VIGNETTE = Identifier.of(
        MyMod.MOD_ID, "textures/vignette.png"
    );

    @Inject(method = "render", at = @At("TAIL"))
    private void myMod$drawVignette(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      color = ColorHelper.fromARGB(INTENSITY, r, g, b); // r, g, b and intensity are controlled by you
      context.drawTexture(
        RenderLayer::getGuiTexturedOverlay,
        MY_VIGNETTE,
        0,
        0,
        0.0f,
        0.0f,
        context.getScaledWindowWidth(),
        context.getScaledWindowHeight(),
        context.getScaledWindowWidth(),
        context.getScaledWindowHeight(),
        color
      );
    }
}
```
Enjoy :)

## Why Does This Work?
We take advantage the fact that vanilla(specifically OpenGL) alpha blends the textures, meaning that if the textures are transparent, they display the content behind them with a mix of the texture itself. Depending on the alpha value, the higher it is, the more the texture covers and opposite applies. The lower the value the more it makes the texture hard to see. The Image(PNG) file transitions to transparent on the center for this reason. It is also black and white to allow for tinting colors correctly and easily

Lastly the reason we call ``RenderLayer::getGuiTexturedOverlay`` and not any other RenderLayer function is because we want to overlay the texture on top of everything else without any blending like how ``RenderLayer::getVignette`` does(to prevent the color inversion)

---
Hope i helped with this post and possibly saved you a lot of the time in the meantime. You may also take a look at [the code in the java file of the repositery](https://github.com/GitBrincie212/Color-Based-Vignettes-Minecraft/blob/main/InGameHUDMixin.java)
