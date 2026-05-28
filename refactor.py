import os
import shutil
import re

base_dir = "/Users/macpro/Projects/fridge-management/app/src/main/java/com/example/android_app"

moves = {
    # Auth
    "ui/sceens/login/LoginScreen.kt": "feature/auth/login/LoginScreen.kt",
    "ui/sceens/login/LoginViewModel.kt": "feature/auth/login/LoginViewModel.kt",
    "ui/sceens/login/RegisterScreen.kt": "feature/auth/register/RegisterScreen.kt",
    
    # Inventory
    "ui/sceens/home/HomeScreen.kt": "feature/inventory/home/HomeScreen.kt",
    "ui/sceens/home/HomeViewModel.kt": "feature/inventory/home/HomeViewModel.kt",
    "viewmodel/FridgeViewModel.kt": "feature/inventory/home/FridgeViewModel.kt",
    "ui/sceens/add/AddManualScreen.kt": "feature/inventory/add/AddManualScreen.kt",
    "ui/sceens/food_detail/FoodDetailScreen.kt": "feature/inventory/detail/FoodDetailScreen.kt",
    "ui/sceens/scanner/ScannerScreen.kt": "feature/inventory/scanner/ScannerScreen.kt",
    
    # Assistant
    "ui/sceens/recipe/RecipeSuggestionScreen.kt": "feature/assistant/recipe/RecipeSuggestionScreen.kt",
    "ui/sceens/recipe/RecipeDetailScreen.kt": "feature/assistant/recipe/RecipeDetailScreen.kt",
    
    # Health
    "ui/sceens/meal/MealScreen.kt": "feature/health/mealplan/MealScreen.kt",
    "ui/sceens/meal/MealViewModel.kt": "feature/health/mealplan/MealViewModel.kt",
    
    # Profile & Shopping
    "ui/sceens/profile/ProfileScreen.kt": "feature/profile/ProfileScreen.kt",
    "ui/sceens/shopping/ShoppingScreen.kt": "feature/shopping/ShoppingScreen.kt",
    
    # Core
    "Navigation.kt": "ui/navigation/Navigation.kt",
    "model/FridgeItem.kt": "domain/model/FridgeItem.kt",
    "data/api/AuthApiService.kt": "data/remote/api/AuthApiService.kt",
    "data/model/AuthModels.kt": "data/remote/dto/AuthModels.kt"
}

print("1. Moving files...")
for old_rel, new_rel in moves.items():
    old_path = os.path.join(base_dir, old_rel)
    new_path = os.path.join(base_dir, new_rel)
    if os.path.exists(old_path):
        os.makedirs(os.path.dirname(new_path), exist_ok=True)
        shutil.move(old_path, new_path)
        print(f"Moved {old_rel} -> {new_rel}")

print("\n2. Cleaning up old dirs...")
for p in ["ui/sceens", "viewmodel", "model", "data/api"]:
    d = os.path.join(base_dir, p)
    if os.path.exists(d):
        try:
            shutil.rmtree(d)
            print(f"Removed {d}")
        except Exception as e:
            print(f"Could not remove {d}: {e}")

print("\n3. Fixing packages and imports...")
for root, _, files in os.walk(base_dir):
    for f in files:
        if f.endswith(".kt"):
            fpath = os.path.join(root, f)
            rel_path = os.path.relpath(root, base_dir)
            
            # expected package
            expected_pkg = "com.example.android_app"
            if rel_path != ".":
                expected_pkg += "." + rel_path.replace(os.sep, ".")
                
            with open(fpath, "r") as file:
                content = file.read()
                
            # Replace package statement
            new_content = re.sub(r"^package\s+[\w\.]+", f"package {expected_pkg}", content, flags=re.MULTILINE)
            
            # Replace imports
            import_replacements = {
                "com.example.android_app.ui.sceens.login.LoginScreen": "com.example.android_app.feature.auth.login.LoginScreen",
                "com.example.android_app.ui.sceens.login.LoginViewModel": "com.example.android_app.feature.auth.login.LoginViewModel",
                "com.example.android_app.ui.sceens.login.RegisterScreen": "com.example.android_app.feature.auth.register.RegisterScreen",
                "com.example.android_app.ui.sceens.home.HomeScreen": "com.example.android_app.feature.inventory.home.HomeScreen",
                "com.example.android_app.ui.sceens.home.HomeViewModel": "com.example.android_app.feature.inventory.home.HomeViewModel",
                "com.example.android_app.viewmodel.FridgeViewModel": "com.example.android_app.feature.inventory.home.FridgeViewModel",
                "com.example.android_app.ui.sceens.add.AddManualScreen": "com.example.android_app.feature.inventory.add.AddManualScreen",
                "com.example.android_app.ui.sceens.food_detail.FoodDetailScreen": "com.example.android_app.feature.inventory.detail.FoodDetailScreen",
                "com.example.android_app.ui.sceens.scanner.ScannerScreen": "com.example.android_app.feature.inventory.scanner.ScannerScreen",
                "com.example.android_app.ui.sceens.recipe.RecipeSuggestionScreen": "com.example.android_app.feature.assistant.recipe.RecipeSuggestionScreen",
                "com.example.android_app.ui.sceens.recipe.RecipeDetailScreen": "com.example.android_app.feature.assistant.recipe.RecipeDetailScreen",
                "com.example.android_app.ui.sceens.meal.MealScreen": "com.example.android_app.feature.health.mealplan.MealScreen",
                "com.example.android_app.ui.sceens.meal.MealViewModel": "com.example.android_app.feature.health.mealplan.MealViewModel",
                "com.example.android_app.ui.sceens.profile.ProfileScreen": "com.example.android_app.feature.profile.ProfileScreen",
                "com.example.android_app.ui.sceens.shopping.ShoppingScreen": "com.example.android_app.feature.shopping.ShoppingScreen",
                "com.example.android_app.model.FridgeItem": "com.example.android_app.domain.model.FridgeItem",
                "com.example.android_app.data.api.AuthApiService": "com.example.android_app.data.remote.api.AuthApiService",
                "com.example.android_app.data.model.": "com.example.android_app.data.remote.dto.",
            }
            
            for old_i, new_i in import_replacements.items():
                new_content = new_content.replace(f"import {old_i}", f"import {new_i}")
            
            # MainActivity and MainApplication might need Navigation import if they use it
            if (f == "MainActivity.kt" or f == "MainApplication.kt") and "import com.example.android_app.ui.navigation.Navigation" not in new_content:
                if "Navigation(" in new_content or "NavigationApp" in new_content: # Actually it's Navigation() in MainActivity normally
                    # Add import right after package declaration
                    new_content = re.sub(r"^(package .*)$", r"\1\nimport com.example.android_app.ui.navigation.Navigation", new_content, flags=re.MULTILINE)
                    
            # Update MainActivity imports if Navigation uses a specific App wrapper
            # E.g. Navigation is a function now.

            if new_content != content:
                with open(fpath, "w") as file:
                    file.write(new_content)
                print(f"Updated imports/package in {fpath}")

print("\nDone.")
