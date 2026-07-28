for file in app/src/main/java/com/example/ui/screens/*.kt app/src/main/java/com/example/ui/dialogs/*.kt app/src/main/java/com/example/ui/components/*.kt; do
  sed -i 's/^Dark$/import com.example.ui.theme.PrimaryTealDark/g' "$file"
  sed -i 's/^Container$/import com.example.ui.theme.LightTealContainer/g' "$file"
  
  # Ensure import com.example.ui.theme.PrimaryTeal is present if PrimaryTeal is used
  if grep -q "PrimaryTeal" "$file"; then
    if ! grep -q "import com.example.ui.theme.PrimaryTeal" "$file"; then
      sed -i '1s/^/import com.example.ui.theme.PrimaryTeal\n/' "$file"
    fi
  fi
done
