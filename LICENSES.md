# Uwagi licencyjne do środowiska szkoleniowego

To repozytorium nie dołącza binariów Docker Desktop ani obrazów kontenerowych. Compose pobiera obrazy z ich oficjalnych rejestrów podczas przygotowania środowiska.

Dla szkolenia na Linux VM rekomendowany jest Docker Engine + Compose plugin, a nie Docker Desktop. Przed szkoleniem organizator powinien:
- zweryfikować własne wymagania licencyjne i polityki organizacji,
- zaakceptować licencje wykorzystywanych projektów,
- pre-pullować/cache'ować obrazy, aby nie zależeć od limitów i internetu podczas zajęć.

Ten plik nie stanowi porady prawnej.
