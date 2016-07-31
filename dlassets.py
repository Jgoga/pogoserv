import sys
import google.protobuf3 as protobuf
from google.protobuf3 import message
from google.protobuf3 import descriptor
sys.modules["google.protobuf"] = protobuf
sys.modules["google.protobuf.message"] = message
sys.modules["google.protobuf.descriptor"] = descriptor

import pgoapi
import argparse
import logging
import itertools
import urllib.request
import os

from POGOProtos.Networking.Responses_pb2 import GetAssetDigestResponse, GetDownloadUrlsResponse
from POGOProtos.Data_pb2 import DownloadUrlEntry, AssetDigestEntry

api = pgoapi.PGoApi()

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-u", "--username", required=True)
    parser.add_argument("-p", "--password", required=True)
    parser.add_argument("-d", "--datadir", required=True)
    parser.add_argument("-A", "--assets", action="store_true")
    parser.add_argument("-M", "--master", action="store_true")
    parser.add_argument("-C", "--clientsettings", action="store_true")
    parser.add_argument("-v", "--verbose", action="store_true")
    args = parser.parse_args()
   
    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)

    if not doauth(args.username, args.password):
        print("!! Failed to auth")
        return

    if args.assets:
        load_assets(args.datadir)

    if args.master:
        load_master(args.datadir)

    if args.clientsettings:
        load_clientsettings(args.datadir)

    print("-> All done!")

def doauth(user, pw):
    print("-> Authing as " + user + ":" + pw)
    # Stupid api required position to be set even though we don't need it
    api.set_position(41.145495, -73.994901, 0)
    return api.login('ptc', user, pw)

def load_clientsettings(datadir):
    print("-> Loading client settings")
    proto = [None]
    api.create_request().download_settings().call(proto)
    dump(datadir + "/CLIENT_SETTINGS.protobuf", proto[0].returns[0])

def load_master(datadir):
    print("-> Loading game_master (item templates)")
    proto = [None]
    api.create_request().download_item_templates().call(proto)
    dump(datadir + "/GAME_MASTER.protobuf", proto[0].returns[0])

def load_assets(datadir):
    print("-> Loading assets")
    #versions = [3100]
    versions = [2903]
    platforms = [1, 2]
    #platforms = [2]

    for ver, plat in itertools.product(versions, platforms):
        digest = fetch_digest(plat, ver)
        dump(datadir + "/ASSET_DIGEST." + str(ver) + "." + str(plat) + ".protobuf", digest.SerializeToString())
        # The api, python protobuf, or something else has a bug (I think) because the checksum field
        # Mystically disappears. So just save the raw bytes.
        urls, urls_raw = fetch_urls(digest)
        dump(datadir + "/DOWNLOAD_URLS." + str(ver) + "." + str(plat) + ".protobuf", urls_raw)

        asset_path = datadir + "/assets/" + str(ver) + "/" + str(plat)
        print("-> Dumping assets to: " + asset_path)
        os.makedirs(asset_path)
        for u in urls.download_urls:
            print("\t* " + u.asset_id)
            req = urllib.request.urlopen(u.url)
            out_path = asset_path + "/" + u.asset_id.replace("/", "-")
            out = open(out_path, "wb")
            out.write(req.read())
            out.close()



# !!! Requires patched version of pgoapi
def fetch_digest(plat, ver):
    print("-> Fetching asset digest for platform=%d, version=%d" % (plat, ver))
    proto = [None]
    api.create_request().get_asset_digest(platform = plat, app_version = ver).call(proto)
    digest = GetAssetDigestResponse()
    digest.ParseFromString(proto[0].returns[0])
    return digest

def fetch_urls(digest):
    assets = [d.asset_id for d in digest.digest]
    print("-> Fetching download urls for %d assets" % len(assets))
    proto = [None]
    api.create_request().get_download_urls(asset_id = assets).call(proto)
    urls = GetDownloadUrlsResponse()
    urls.ParseFromString(proto[0].returns[0])
    return urls, proto[0].returns[0]

def dump(f, p):
    print("-> Dumping to " + f)
    out = open(f, "wb")
    out.write(p)
    out.close()


if __name__ == "__main__":
    main()

