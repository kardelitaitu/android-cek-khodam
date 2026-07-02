import os
import csv

def sanitize_filename(name):
    return name.lower().replace(" ", "").replace("(", "").replace(")", "").replace("-", "").replace("_", "")

def load_csv(filepath):
    rows = []
    if not os.path.exists(filepath):
        return rows
    with open(filepath, mode='r', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        for row in reader:
            if row:
                rows.append(row)
    return rows

def main():
    assets_dir = "app/src/main/assets"
    elements = load_csv(os.path.join(assets_dir, "elements.csv"))
    beasts = load_csv(os.path.join(assets_dir, "beasts.csv"))
    jokes = load_csv(os.path.join(assets_dir, "jokes.csv"))
    flowers = load_csv(os.path.join(assets_dir, "flowers.csv"))

    output_file = "comfyui_prompts.txt"
    with open(output_file, mode='w', encoding='utf-8') as out:
        out.write("=== CEK KHODAM COMFYUI PROMPT LIST (450 TOTAL COMBINATIONS) ===\n\n")

        # 1. Element-Beast Combinations (300 prompts)
        out.write("--- POOL 1: ELEMENT-BEAST COMBINATIONS (300 COMBINATIONS) ---\n")
        prompt_idx = 0
        for element in elements:
            e_name = element[1]
            e_desc = element[2]
            
            # Map element to colors
            color_map = {
                "Wood": "Emerald Green",
                "Fire": "Solar Red",
                "Water": "Cosmic Blue",
                "Metal": "Chrome Silver",
                "Earth": "Golden Yellow",
                "Cosmos": "Nebula Purple",
                "Wind": "Teal Sky",
                "Thunder": "Electric Orange",
                "Light": "Radiant White"
            }
            
            color = "Gold"
            for key, val in color_map.items():
                if key.lower() in e_name.lower():
                    color = val
                    break

            for beast in beasts:
                b_name = beast[1]
                b_desc = beast[2]
                
                khodam_name = f"{e_name} {b_name}"
                filename = f"khodam_{sanitize_filename(khodam_name)}.png"
                
                prompt = (
                    f"A high-fidelity mystical {khodam_name} spirit avatar, {b_desc}, "
                    f"influenced by the essence of {e_name} which is {e_desc}. "
                    f"Stylized game card illustration, detailed creature mascot, dark cosmic space background with colorful stardust and nebulae, "
                    f"glowing neon {color} outline, epic fantasy concept art, centered square composition, game asset style."
                )
                
                out.write(f"Index: {prompt_idx}\n")
                out.write(f"Khodam: {khodam_name}\n")
                out.write(f"Filename: {filename}\n")
                out.write(f"Prompt: {prompt}\n")
                out.write("-" * 40 + "\n")
                prompt_idx += 1

        # 2. Jokes Combinations (50 prompts)
        out.write("\n--- POOL 2: JOKE/MEME COMBINATIONS (50 COMBINATIONS) ---\n")
        for joke in jokes:
            j_name = joke[1]
            j_desc = joke[2]
            filename = f"khodam_{sanitize_filename(j_name)}.png"
            
            prompt = (
                f"A funny, humorous cartoon illustration of a {j_name}, {j_desc}. "
                f"Stylized goofy game card artwork, detailed mascot, dark cosmic space background with sparkling comical stars, "
                f"glowing neon Orange outline, epic meme fantasy concept art, centered square composition, comic game asset style."
            )
            
            out.write(f"Index: {prompt_idx}\n")
            out.write(f"Khodam: {j_name}\n")
            out.write(f"Filename: {filename}\n")
            out.write(f"Prompt: {prompt}\n")
            out.write("-" * 40 + "\n")
            prompt_idx += 1

        # 3. Flowers Combinations (100 prompts)
        out.write("\n--- POOL 3: FLOWER COMBINATIONS (100 COMBINATIONS) ---\n")
        for flower in flowers:
            f_name = flower[1]
            f_desc = flower[2]
            filename = f"khodam_{sanitize_filename(f_name)}.png"
            
            prompt = (
                f"A beautiful elegant mystical {f_name} flower, {f_desc}. "
                f"Stylized floral game card illustration, detailed botanical mascot, dark cosmic space background with glowing floral pollen and stardust, "
                f"glowing neon Pink outline, epic fantasy concept art, centered square composition, game asset style."
            )
            
            out.write(f"Index: {prompt_idx}\n")
            out.write(f"Khodam: {f_name}\n")
            out.write(f"Filename: {filename}\n")
            out.write(f"Prompt: {prompt}\n")
            out.write("-" * 40 + "\n")
            prompt_idx += 1

    print(f"Success! Generated 450 prompts in '{output_file}'.")

if __name__ == "__main__":
    main()
