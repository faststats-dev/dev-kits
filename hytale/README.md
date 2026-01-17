# Hytale Module

Since the Hytale API is not public yet, and redistribution is not allowed,
you have to download the Hytale server yourself.

## Initial Setup

Before building this module, you need to authenticate with your Hytale account. You have two options:

### Option 1: Using Environment Variable (Recommended for CI)

Set the `HYTALE_DOWNLOADER_CREDENTIALS` environment variable with your Hytale authentication credentials:

```bash
export HYTALE_DOWNLOADER_CREDENTIALS='{"your":"auth","json":"here"}'
./gradlew :hytale:download-server
```

This token can be obtained by running the download task without credentials once (
see [Obtaining Hytale Authentication](#obtaining-hytale-authentication)).

### Option 2: Using Credentials File (Recommended for Local Development)

1. Create `.hytale-downloader-credentials.json` in the `hytale/` directory
2. Paste your Hytale authentication JSON credentials in the file
3. Run the download task:

```bash
./gradlew :hytale:download-server
```

The credentials file is gitignored and won't be committed.

## Obtaining Hytale Authentication

To get your Hytale authentication credentials:

1. Run the download task without credentials:
   ```bash
   ./gradlew :hytale:download-server
   ```
2. The Hytale downloader will prompt you to authenticate
3. After successful authentication, the credentials will be saved to
   `.hytale-downloader-credentials.json` for future use

## Updating the Server

To update the Hytale server:

```bash
./gradlew :hytale:update-server
```