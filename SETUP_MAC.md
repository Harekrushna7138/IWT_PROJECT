# Mac Setup (One Time)

Run these in Terminal to fix Homebrew permissions and install required tools:

```bash
sudo chown -R "$USER" /opt/homebrew /Users/$USER/Library/Logs/Homebrew
chmod u+w /opt/homebrew /Users/$USER/Library/Logs/Homebrew
brew install openjdk@17 maven mysql tomcat
brew services start mysql
brew services start tomcat
```

Add Java to shell (zsh):

```bash
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Initialize DB:

```bash
cd /Users/harekrushna/Desktop/project1_lib
mysql -u root -p < database.sql
```

Run app:

```bash
cd /Users/harekrushna/Desktop/project1_lib
./run-local.sh
```

Open:

`http://localhost:8080/library-room-booking`

Admin login:

- Email: `admin@college.edu`
- Password: `admin123`
