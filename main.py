from dotenv import load_dotenv
import os
import base64
from requests import post
import requests
import json
import sys
import io

#print(os.getcwd())

#GETS SCRIPT DIRECTORY
script_dir = os.path.dirname(os.path.realpath(__file__))
#Defines relative path to file
file_path = os.path.join(script_dir, "spotifyPlaylist.json")

# Override the stdout encoding
# Helps with any special characters or emojis used in the playlist
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

load_dotenv()

with open(file_path, "r") as file:
    playlist_link = file.read()

playlist_url = playlist_link.strip('"')
playlist_key = playlist_url.strip('https://open.spotify.com/playlist/')

if "?" in playlist_key :
    playlist_key = playlist_key.split("?", 1)[0]

client_id = os.getenv("CLIENT_ID")
client_secret = os.getenv("CLIENT_SECRET")

#Get token used to retrieve data from spotify web api
def get_token():
    auth_string = client_id + ":" + client_secret
    auth_bytes = auth_string.encode("utf-8")
    auth_base64 = str(base64.b64encode(auth_bytes), "utf-8")

    url = "https://accounts.spotify.com/api/token"

    headers = {
        "Authorization": "Basic " + auth_base64,
        "Content-Type": "application/x-www-form-urlencoded"
    }
    data = {"grant_type": "client_credentials"}

    result = post(url, headers=headers, data=data)
    json_result = json.loads(result.content)
    token = json_result["access_token"]
    return token

token = get_token()  # Must use this to retrieve data

#playlist_id = '1ljvjezq3skwALbYYwJk4G'
playlist_url = f'https://api.spotify.com/v1/playlists/{playlist_key}/tracks'

# Set up headers including the access token
headers = {
    'Authorization': f'Bearer {token}'
}

# Make request
playlist_response = requests.get(playlist_url, headers=headers)

# Check for successful response
if playlist_response.status_code != 200:
    print("Error:", playlist_response.status_code)
    print("Response Text:", playlist_response.text)
else:
    # Get playlist information as a JSON file
    playlist_data = playlist_response.json()

    # Make request
playlist_response = requests.get(playlist_url, headers=headers)

# Check for successful response
if playlist_response.status_code != 200:
    print("Error:", playlist_response.status_code)
    print("Response Text:", playlist_response.text)
else:
    # Get playlist information as a JSON file
    playlist_data = playlist_response.json()

#List to store all the genres in an empty list
all_genres = []

if playlist_data:
    for item in playlist_data['items']:
        track = item['track']

        #Pull artist id from track in playlist
        artist_id = track['artists'][0]['id']

        #Use artist ID for thr url used to make the get request
        artist_url = f'https://api.spotify.com/v1/artists/{artist_id}'
        artist_response = requests.get(artist_url, headers=headers)
        artist_data = artist_response.json()

        if 'genres' in artist_data:
            genres = artist_data['genres']
            all_genres.extend(genres)
            print(f"{genres}")
        else:
            all_genres.append("N/A")
            print(f"N/A")
