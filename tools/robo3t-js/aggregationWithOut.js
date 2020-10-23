db.getCollection('rearrangement').aggregate([ 
  {"$match": 
      {"repertoire_id": 
          { "$in":["978739827430911510-242ac11a-0001-012","967272264750591510-242ac11a-0001-012","954859809265151510-242ac11a-0001-012","943435196257791510-242ac11a-0001-012","932611878671871510-242ac11a-0001-012","921831510758911510-242ac11a-0001-012","910621646116351510-242ac11a-0001-012","899239982781951510-242ac11a-0001-012","888803212252671510-242ac11a-0001-012","877765146301951510-242ac11a-0001-012","867629023483391510-242ac11a-0001-012","857364051645951510-242ac11a-0001-012","847399727519231510-242ac11a-0001-012","836662309279231510-242ac11a-0001-012","826397337441791510-242ac11a-0001-012","816905459717631510-242ac11a-0001-012","806640487880191510-242ac11a-0001-012","797277459174911510-242ac11a-0001-012","787484933740031510-242ac11a-0001-012","777005213537791510-242ac11a-0001-012","766482543662591510-242ac11a-0001-012","755530377057791510-242ac11a-0001-012","745007707182591510-242ac11a-0001-012","735172232074751510-242ac11a-0001-012","724649562199551510-242ac11a-0001-012","714083942651391510-242ac11a-0001-012","704076668851711510-242ac11a-0001-012","693210401592831510-242ac11a-0001-012","681957587277311510-242ac11a-0001-012","671048370345471510-242ac11a-0001-012","661384693929471510-242ac11a-0001-012","651033822746111510-242ac11a-0001-012","7769654456084459030-242ac116-0001-012","7759389484247019030-242ac116-0001-012","7749511059466219030-242ac116-0001-012","7737742849075179030-242ac116-0001-012","7727563776583659030-242ac116-0001-012","7716439811287019030-242ac116-0001-012","7707076782581739030-242ac116-0001-012","7696940659763179030-242ac116-0001-012","7686890436290539030-242ac116-0001-012","7677828055295979030-242ac116-0001-012","7668121429207019030-242ac116-0001-012","7657169262602219030-242ac116-0001-012","7619416500070379030-242ac116-0001-012","7609967572019179030-242ac116-0001-012","7600432744622059030-242ac116-0001-012","7590726118533099030-242ac116-0001-012","7578528411412459030-242ac116-0001-012","7569380131071979030-242ac116-0001-012","7562465233725419030-242ac116-0001-012","7555679185397739030-242ac116-0001-012","7546917452113899030-242ac116-0001-012","7537210826024939030-242ac116-0001-012","7527504199935979030-242ac116-0001-012","7517711674501099030-242ac116-0001-012","7507360803317739030-242ac116-0001-012","7497525328209899030-242ac116-0001-012","7488205249177579030-242ac116-0001-012","7479701213931499030-242ac116-0001-012","7469779839477739030-242ac116-0001-012","1841923116114776551-242ac11c-0001-012","1602908186092376551-242ac11c-0001-012","2366080924918616551-242ac11c-0001-012","2541616238306136551-242ac11c-0001-012","1993707260355416551-242ac11c-0001-012","2197374609531736551-242ac11c-0001-012","2848663450297176551-242ac11c-0001-012","2685411743376216551-242ac11c-0001-012","3438706057421656551-242ac11c-0001-012","3628844259615576551-242ac11c-0001-012","2989624276951896551-242ac11c-0001-012","3252733973504856551-242ac11c-0001-012","4181735399629656551-242ac11c-0001-012","3924638657291096551-242ac11c-0001-012","4744762662462296551-242ac11c-0001-012","4931851437876056551-242ac11c-0001-012","4357957907784536551-242ac11c-0001-012","4476756703191896551-242ac11c-0001-012","5531257073705816551-242ac11c-0001-012","5215877625160536551-242ac11c-0001-012","6205695788196696551-242ac11c-0001-012","6393557657723736551-242ac11c-0001-012","7158276584776536551-242ac11c-0001-012","5953881855632216551-242ac11c-0001-012","6819446614795096551-242ac11c-0001-012","7972301736387416551-242ac11c-0001-012","7461458326201176551-242ac11c-0001-012","7640859110155096551-242ac11c-0001-012","6964444710708056551-242ac11c-0001-012","7313153105470296551-242ac11c-0001-012","8112833066312536551-242ac11c-0001-012","7793588147200856551-242ac11c-0001-012","8602072790999896551-242ac11c-0001-012","8733756488295256551-242ac11c-0001-012","8263242821018456551-242ac11c-0001-012","8425807333172056551-242ac11c-0001-012","7885350151947415065-242ac11c-0001-012","8945756074025816551-242ac11c-0001-012","7309695685264535065-242ac11c-0001-012","8485700680582295065-242ac11c-0001-012","9084118473933975065-242ac11c-0001-012","8961797805343895065-242ac11c-0001-012","7745763714827415065-242ac11c-0001-012","6738379135550615065-242ac11c-0001-012","5624006920930455065-242ac11c-0001-012","7066128089908375065-242ac11c-0001-012","7591789137265815065-242ac11c-0001-012","7446748091679895065-242ac11c-0001-012","6576544767837335065-242ac11c-0001-012","6880327804683415065-242ac11c-0001-012","6088937130722455065-242ac11c-0001-012","5939858815878295065-242ac11c-0001-012","6389112395039895065-242ac11c-0001-012","6240077029868695065-242ac11c-0001-012","5462430251254935065-242ac11c-0001-012","5765010697258135065-242ac11c-0001-012","5039977268020375065-242ac11c-0001-012","4858300151399575065-242ac11c-0001-012","5338391595746455065-242ac11c-0001-012","5168912186246295065-242ac11c-0001-012","1087720534299120106-242ac116-0001-012","1099832342073840106-242ac116-0001-012","1111171055735280106-242ac116-0001-012","1123111064818160106-242ac116-0001-012","1134578627498480106-242ac116-0001-012","1146432737235440106-242ac116-0001-012","1157513752859120106-242ac116-0001-012","1168852466520560106-242ac116-0001-012","1179761683452400106-242ac116-0001-012","1191486944170480106-242ac116-0001-012","1204586594423280106-242ac116-0001-012","1216870200889840106-242ac116-0001-012","1227736468148720106-242ac116-0001-012","1237443094237680106-242ac116-0001-012","1249511952339440106-242ac116-0001-012","1261022464692720106-242ac116-0001-012","1272919524102640106-242ac116-0001-012","1283871690707440106-242ac116-0001-012","1294265511563760106-242ac116-0001-012","1305690124571120106-242ac116-0001-012","1318145529729520106-242ac116-0001-012","1329441293718000106-242ac116-0001-012","1341166554436080106-242ac116-0001-012","1353664909267440106-242ac116-0001-012","1365046572601840106-242ac116-0001-012","1375955789533680106-242ac116-0001-012","1386779107119600106-242ac116-0001-012","1397903072416240106-242ac116-0001-012","1409069987385840106-242ac116-0001-012","1420408701047280106-242ac116-0001-012","1432005112746480106-242ac116-0001-012","1441539940143600106-242ac116-0001-012","1449915126370800106-242ac116-0001-012","1462241682510320106-242ac116-0001-012","1473666295517680106-242ac116-0001-012","1487238392173040106-242ac116-0001-012","1498663005180400106-242ac116-0001-012","1510259416879600106-242ac116-0001-012","1521211583484400106-242ac116-0001-012","1532464397799920106-242ac116-0001-012","1543931960480240106-242ac116-0001-012","1555485422506480106-242ac116-0001-012","1566566438130160106-242ac116-0001-012","1577432705389040106-242ac116-0001-012","1589501563490800106-242ac116-0001-012","1600668478460400106-242ac116-0001-012","1611706544411120106-242ac116-0001-012","1622787560034800106-242ac116-0001-012","1634126273696240106-242ac116-0001-012","1645507937030640106-242ac116-0001-012","5376632132572615146-242ac116-0001-012","5385565664548295146-242ac116-0001-012","5394885743580615146-242ac116-0001-012","5406482155279815146-242ac116-0001-012","5417863818614215146-242ac116-0001-012","5428944834237895146-242ac116-0001-012","5440970742666695146-242ac116-0001-012","5458236511196615146-242ac116-0001-012","5470520117663175146-242ac116-0001-012","5482460126746055146-242ac116-0001-012","5494357186155975146-242ac116-0001-012","5506640792622535146-242ac116-0001-012","5518752600397255146-242ac116-0001-012","5530434911442375146-242ac116-0001-012","5542331970852295146-242ac116-0001-012","5553713634186695146-242ac116-0001-012","5570120409257415146-242ac116-0001-012","5581587971937735146-242ac116-0001-012","5593270282982855146-242ac116-0001-012","5604651946317255146-242ac116-0001-012","5616033609651655146-242ac116-0001-012","5627372323313095146-242ac116-0001-012","5639226433050055146-242ac116-0001-012","5651252341478855146-242ac116-0001-012","5662805803505095146-242ac116-0001-012","5674230416512455146-242ac116-0001-012","5685697979192775146-242ac116-0001-012","5697208491546055146-242ac116-0001-012","5743808886707655146-242ac116-0001-012","5755791845463495146-242ac116-0001-012","5767989552584135146-242ac116-0001-012","5777438480635335146-242ac116-0001-012","5787789351818695146-242ac116-0001-012","5799557562209735146-242ac116-0001-012","5810767426852295146-242ac116-0001-012","5822406788224455146-242ac116-0001-012","5838427016238535146-242ac116-0001-012","5850023427937735146-242ac116-0001-012","5861362141599175146-242ac116-0001-012","5873130351990215146-242ac116-0001-012","5884683814016455146-242ac116-0001-012","5897182168847815146-242ac116-0001-012","5909465775314375146-242ac116-0001-012","5927934134687175146-242ac116-0001-012","5940260690826695146-242ac116-0001-012","5952544297293255146-242ac116-0001-012","5964999702451655146-242ac116-0001-012","5976553164477895146-242ac116-0001-012","5988235475523015146-242ac116-0001-012","6004771099612615146-242ac116-0001-012","6016066863601095146-242ac116-0001-012","6027577375954375146-242ac116-0001-012","6039130837980615146-242ac116-0001-012","6050426601969095146-242ac116-0001-012","6061765315630535146-242ac116-0001-012","6072803381581255146-242ac116-0001-012","6084185044915655146-242ac116-0001-012","6095738506941895146-242ac116-0001-012","6106991321257415146-242ac116-0001-012","6118330034918855146-242ac116-0001-012","6129926446618055146-242ac116-0001-012","6141737606682055146-242ac116-0001-012","6153248119035335146-242ac116-0001-012","6164930430080455146-242ac116-0001-012","6176741590144455146-242ac116-0001-012","6193191314888135146-242ac116-0001-012","6204959525279175146-242ac116-0001-012","6216255289267655146-242ac116-0001-012","6227551053256135146-242ac116-0001-012","6239147464955335146-242ac116-0001-012","6251302222403015146-242ac116-0001-012","6268224393549255146-242ac116-0001-012","6279520157537735146-242ac116-0001-012","6291331317601735146-242ac116-0001-012","6303185427338695146-242ac116-0001-012","6315726731843015146-242ac116-0001-012","6327323143542215146-242ac116-0001-012","6342999774172615146-242ac116-0001-012","6354295538161095146-242ac116-0001-012","6365591302149575146-242ac116-0001-012","6377316562867655146-242ac116-0001-012","6389041823585735146-242ac116-0001-012","6405105001272775146-242ac116-0001-012","6417388607739335146-242ac116-0001-012","6429543365187015146-242ac116-0001-012","6440710280156615146-242ac116-0001-012","6451877195126215146-242ac116-0001-012","6463817204209095146-242ac116-0001-012","6475155917870535146-242ac116-0001-012","6486537581204935146-242ac116-0001-012","6497876294866375146-242ac116-0001-012","6509129109181895146-242ac116-0001-012"]} 
      } }, 
  { "$group": {"_id": "$repertoire_id", "count":{"$sum":"1"} } }, 
  { "$out" : "10.22.2020-tst" } 
])