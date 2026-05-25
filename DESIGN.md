---
name: SmartBite UI
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#454937'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f1f1f1'
  outline: '#757965'
  outline-variant: '#c5c9b1'
  surface-tint: '#506600'
  primary: '#506600'
  on-primary: '#ffffff'
  primary-container: '#a4c639'
  on-primary-container: '#3e5000'
  inverse-primary: '#b1d446'
  secondary: '#586062'
  on-secondary: '#ffffff'
  secondary-container: '#dae1e3'
  on-secondary-container: '#5d6466'
  tertiary: '#006b55'
  on-tertiary: '#ffffff'
  tertiary-container: '#38cfaa'
  on-tertiary-container: '#005442'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ccf05f'
  primary-fixed-dim: '#b1d446'
  on-primary-fixed: '#161e00'
  on-primary-fixed-variant: '#3c4d00'
  secondary-fixed: '#dde4e6'
  secondary-fixed-dim: '#c1c8ca'
  on-secondary-fixed: '#161d1f'
  on-secondary-fixed-variant: '#41484a'
  tertiary-fixed: '#6dfad2'
  tertiary-fixed-dim: '#4bddb7'
  on-tertiary-fixed: '#002018'
  on-tertiary-fixed-variant: '#005140'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -1px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.5px
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 1px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  margin-mobile: 20px
  gutter: 16px
  stack-sm: 12px
  stack-md: 24px
  container-padding: 16px
---

## Brand & Style

The design system is centered on the concept of "Fresh Efficiency." It targets health-conscious individuals and busy households who value organization and food waste reduction. The UI evokes a "sterile yet friendly" atmosphere, mimicking the clean, organized interior of a high-end modern refrigerator.

The aesthetic blends **Modern Minimalism** with **Soft Glassmorphism**. It prioritizes high legibility, abundant white space, and a vibrant primary palette to feel energetic and trustworthy. The interface should feel breathable and lightweight, avoiding heavy textures in favor of light-refracting surfaces and soft, organic shapes.

## Colors

The palette is anchored by a signature **Lime Green** (#a4c639), representing freshness and vitality. This is supported by a neutral foundation of **Pure White** and **Cool Gray** to maintain a "clinical-clean" look. 

Functional color logic is critical for the "Smart Fridge" experience:
- **Primary:** Actionable elements and brand highlights.
- **Surface:** Use #ffffff for primary cards and #f9f9f9 for background canvas to create subtle contrast.
- **Status Indicators:** A traffic-light system for food longevity:
    - **Fresh:** Lime Green (The brand primary).
    - **Warning:** Soft Amber for items expiring within 48 hours.
    - **Expiring:** Muted Coral for items that need immediate consumption.

## Typography

This design system utilizes **Plus Jakarta Sans** exclusively. Its modern, geometric construction and naturally rounded terminals perfectly complement the "friendly-sterile" brand personality.

- **Headlines:** Use Bold (700) weights with tighter letter spacing for a punchy, contemporary feel.
- **Body Text:** Use Regular (400) weight for maximum readability against light backgrounds.
- **Labels:** Use Bold Caps for status categories (e.g., "DAIRY," "PRODUCE") to provide clear hierarchy in dense list views.

## Layout & Spacing

The system follows an **8px hard grid** to ensure mathematical harmony. For the mobile-first experience:
- **Margins:** A generous 20px side margin keeps content away from the edges of modern curved displays.
- **Fluidity:** Use a single-column fluid layout for content feeds, with 2-column grids reserved for category browsing (e.g., Fridge vs. Pantry tiles).
- **Vertical Rhythm:** Elements are grouped using 12px (small) and 24px (large) stacks to separate different food categories and metadata groups.

## Elevation & Depth

Depth is achieved through a combination of **Tonal Layering** and **Soft Shadows**. 

1.  **The Canvas:** The base layer is #f9f9f9.
2.  **Primary Cards:** Objects (Food items, recipes) sit on #ffffff surfaces with a `shadow-sm` (4px blur, 2% opacity black).
3.  **Active/Floating Elements:** Primary buttons and active selection cards use a `shadow-md` (12px blur, 6% opacity) with a tiny hint of the primary green color in the shadow tint.
4.  **Overlays:** Modals and bottom sheets utilize **Glassmorphism**. Use a backdrop-blur (12px) and a semi-transparent white fill (80% opacity) to maintain the "chilled glass" aesthetic of a fridge.

## Shapes

The shape language is highly organic and approachable. 
- **Base Components:** Input fields, small buttons, and list items use 8px (0.5rem) corner radii.
- **Containers:** Content cards and feature blocks use **Rounded-XL** (1.5rem/24px) to create a soft, friendly container for item imagery.
- **Interactive Pill:** Use full pill-shaped rounding for chips and status tags to differentiate them from functional buttons.

## Components

### Buttons
- **Primary:** Solid #a4c639 with white text. High roundedness (12px).
- **Secondary:** Transparent background with a 1.5px border of the primary color.
- **Icon Buttons:** Circular containers with a soft gray background (#f1f2f6).

### Chips (Freshness Tags)
- Small, pill-shaped tags used for "Days Left" or "Category." 
- Use a low-opacity version of the status color for the background (10%) and the full-strength color for the text.

### Cards (Fridge Items)
- White background, 24px corner radius.
- Must include a dedicated area for the "Freshness Indicator" (a vertical 4px bar on the left edge or a prominent colored dot).
- Images should have a subtle 1px inner stroke to prevent "bleeding" into the white card surface.

### Input Fields
- Subtle gray background (#f1f2f6) with no border. 
- On focus, transition to a white background with a 2px #a4c639 border.

### Status Indicators
- **Icons:** Use Material Symbols (Outlined). 
- Use "Leaf" icons for organic items and "Kitchen" for processed goods.
- High-priority alerts (Expiring) should use a subtle pulse animation on the icon.