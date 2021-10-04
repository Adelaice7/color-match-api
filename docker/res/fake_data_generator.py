import csv
from faker import Faker

def datagenerate(records, headers):
    fake = Faker('fr_FR')
    with open("Fake_Product_data_200k.csv", 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=headers)
        writer.writeheader()
        
        for i in range(records):
            composition_element = fake.random_element(elements=('Coton', 'Elasthanne', 'Polyester', 'Laine', 'Polyamide', 'Lyocell'))
            composition = str(fake.random_int(min=1, max=100)) + '% ' + composition_element
            sleeve = fake.random_element(elements=('Manches courtes', 'Manches longues', 'Manches aux coudes'))
            
            writer.writerow({
                    "id" : fake.bothify('??###-##-?#?', letters='ABCDEFGHIJKLMNOPQRSTUVWXYZ'),
                    "title" : 'Polo ' + fake.word() + ' ' + fake.word(),
                    "gender_id": fake.random_element(elements=('MAN', 'WOM', 'BOY', 'GIR')),
                    "composition" : composition,
                    "sleeve" : sleeve,
                    "photo": fake.file_path(depth=3, extension='.jpg'),
                    "url" : fake.image_url()
                    })
    
if __name__ == '__main__':
    records = 200000
    headers = ["id", "title", "gender_id", "composition", "sleeve", "photo", "url"]
    datagenerate(records, headers)
    print("CSV generation complete!")