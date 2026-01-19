import {createResource, createSignal, For, Show} from "solid-js";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "~/components/ui/card";
import {TextField, TextFieldInput, TextFieldLabel} from "~/components/ui/text-field";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "~/components/ui/select";
import {Button} from "~/components/ui/button";
import {client} from "~/lib/api";
import Loading from "~/components/Loading";
import {Bed, Search, Star} from "lucide-solid";

const roomEndpoint = client.path("/api/v1/rooms").method("get").create();
const standardEndpoint = client.path("/api/v1/standards").method("get").create();
const currencyEndpoint = client.path("/api/v1/currency/{code}").method("get").create();

type Currency = "PLN" | "USD" | "JPY";

export default function ReceptionView() {
    // Filter state
    const [roomName, setRoomName] = createSignal("");
    const [selectedStandard, setSelectedStandard] = createSignal<string>("");
    const [minBeds, setMinBeds] = createSignal<string>("");
    const [minPrice, setMinPrice] = createSignal<string>("");
    const [maxPrice, setMaxPrice] = createSignal<string>("");
    const [currency, setCurrency] = createSignal<Currency>("PLN");

    // Load standards for filter dropdown
    const [standards] = createResource(async () => {
        const {ok, data} = await standardEndpoint({});
        return ok ? data.content : [];
    });

    // Load currency rates
    const [usdRate] = createResource(
        () => currency() === "USD",
        async (shouldLoad) => {
            if (!shouldLoad) return null;
            const {ok, data} = await currencyEndpoint({code: "USD"});
            return ok && data.mid ? data.mid : null;
        }
    );

    const [jpyRate] = createResource(
        () => currency() === "JPY",
        async (shouldLoad) => {
            if (!shouldLoad) return null;
            const {ok, data} = await currencyEndpoint({code: "JPY"});
            return ok && data.mid ? data.mid : null;
        }
    );

    // Load rooms with filters
    const [rooms, {refetch}] = createResource(async () => {
        const filters: Record<string, any> = {};

        if (roomName()) filters.name = roomName();
        if (selectedStandard()) filters.standardId = parseInt(selectedStandard());
        if (minBeds()) filters.minBedsAvailable = parseInt(minBeds());
        if (minPrice()) filters.minBasePrice = parseFloat(minPrice());
        if (maxPrice()) filters.maxBasePrice = parseFloat(maxPrice());

        const {ok, data} = await roomEndpoint(filters);
        return ok ? data.content : [];
    });

    const convertPrice = (pricePLN: number | null | undefined): string => {
        if (!pricePLN) return "N/A";

        const curr = currency();
        if (curr === "PLN") return `${pricePLN.toFixed(2)} PLN`;

        if (curr === "USD") {
            const rate = usdRate();
            return rate ? `${(pricePLN / rate).toFixed(2)} USD` : "...";
        }

        if (curr === "JPY") {
            const rate = jpyRate();
            return rate ? `${(pricePLN / rate).toFixed(0)} JPY` : "...";
        }

        return `${pricePLN.toFixed(2)} PLN`;
    };

    const handleSearch = () => {
        refetch();
    };

    return (
        <div class="p-6 space-y-6 size-full overflow-auto">
            <div class="flex items-center justify-between">
                <h1 class="text-3xl font-bold">Recepcja - Wyszukiwanie Pokoi</h1>
            </div>

            {/* Filters */}
            <Card>
                <CardHeader>
                    <CardTitle>Filtry wyszukiwania</CardTitle>
                    <CardDescription>Znajdź odpowiedni pokój dla gościa</CardDescription>
                </CardHeader>
                <CardContent>
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        <TextField>
                            <TextFieldLabel>Nazwa pokoju</TextFieldLabel>
                            <TextFieldInput
                                type="text"
                                placeholder="Wpisz nazwę..."
                                value={roomName()}
                                onInput={(e) => setRoomName(e.currentTarget.value)}
                            />
                        </TextField>

                        <div class="space-y-2">
                            <label class="text-sm font-medium">Standard</label>
                            <Select
                                value={selectedStandard()}
                                onChange={setSelectedStandard}
                                options={standards() || []}
                                placeholder="Wybierz standard..."
                                itemComponent={(props) => (
                                    <SelectItem item={props.item}>{props.item.rawValue.name}</SelectItem>
                                )}
                            >
                                <SelectTrigger>
                                    <SelectValue<any>>
                                        {(state) => {
                                            const selected = (standards() || []).find((s: any) => s.id?.toString() === selectedStandard());
                                            return selected?.name || "Wybierz standard...";
                                        }}
                                    </SelectValue>
                                </SelectTrigger>
                                <SelectContent/>
                            </Select>
                        </div>

                        <TextField>
                            <TextFieldLabel>Min. liczba łóżek</TextFieldLabel>
                            <TextFieldInput
                                type="number"
                                min="1"
                                placeholder="np. 2"
                                value={minBeds()}
                                onInput={(e) => setMinBeds(e.currentTarget.value)}
                            />
                        </TextField>

                        <TextField>
                            <TextFieldLabel>Cena min (PLN)</TextFieldLabel>
                            <TextFieldInput
                                type="number"
                                min="0"
                                step="0.01"
                                placeholder="np. 100"
                                value={minPrice()}
                                onInput={(e) => setMinPrice(e.currentTarget.value)}
                            />
                        </TextField>

                        <TextField>
                            <TextFieldLabel>Cena max (PLN)</TextFieldLabel>
                            <TextFieldInput
                                type="number"
                                min="0"
                                step="0.01"
                                placeholder="np. 500"
                                value={maxPrice()}
                                onInput={(e) => setMaxPrice(e.currentTarget.value)}
                            />
                        </TextField>

                        <div class="space-y-2">
                            <label class="text-sm font-medium">Waluta wyświetlania</label>
                            <Select
                                value={currency()}
                                onChange={(val) => setCurrency(val as Currency)}
                                options={["PLN", "USD", "JPY"]}
                                placeholder="PLN"
                                itemComponent={(props) => <SelectItem
                                    item={props.item}>{props.item.rawValue}</SelectItem>}
                            >
                                <SelectTrigger>
                                    <SelectValue<string>>{(state) => state.selectedOption() || "PLN"}</SelectValue>
                                </SelectTrigger>
                                <SelectContent/>
                            </Select>
                        </div>
                    </div>

                    <div class="mt-4">
                        <Button onClick={handleSearch} class="w-full md:w-auto">
                            <Search class="mr-2 size-4"/>
                            Szukaj pokoi
                        </Button>
                    </div>
                </CardContent>
            </Card>

            {/* Results */}
            <Show when={!rooms.loading} fallback={<Loading text="Wczytywanie pokoi..."/>}>
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    <For each={rooms()} fallback={
                        <Card class="col-span-full">
                            <CardContent class="p-6 text-center text-muted-foreground">
                                Nie znaleziono pokoi spełniających kryteria
                            </CardContent>
                        </Card>
                    }>
                        {(room) => {
                            const [standard] = createResource(
                                () => room.standardId,
                                async (id) => {
                                    const endpoint = client.path("/api/v1/standards/{idOrUuid}").method("get").create();
                                    const {ok, data} = await endpoint({idOrUuid: id.toString()});
                                    return ok ? data : null;
                                }
                            );

                            return (
                                <Card class="hover:shadow-lg transition-shadow">
                                    <CardHeader>
                                        <CardTitle class="flex items-center gap-2">
                                            <Bed class="size-5"/>
                                            {room.name}
                                        </CardTitle>
                                        <CardDescription>
                                            <Show when={!standard.loading && standard()}
                                                  fallback="Ładowanie standardu...">
                                                <div class="flex items-center gap-1">
                                                    <Star class="size-4"/>
                                                    {standard()?.name}
                                                </div>
                                            </Show>
                                        </CardDescription>
                                    </CardHeader>
                                    <CardContent class="space-y-3">
                                        <div class="flex items-center justify-between text-sm">
                                            <span class="text-muted-foreground">Dostępne łóżka:</span>
                                            <span class="font-semibold">{room.bedsAvailable}</span>
                                        </div>

                                        <Show when={room.roomDescription}>
                                            <p class="text-sm text-muted-foreground line-clamp-2">{room.roomDescription}</p>
                                        </Show>

                                        <div class="pt-3 border-t space-y-2">
                                            <div class="flex items-center justify-between">
                                                <span class="text-sm text-muted-foreground">Cena bazowa:</span>
                                                <span class="font-bold text-primary flex items-center gap-1">
                                                    {convertPrice(room.basePriceOverride ?? standard()?.basePrice)}
                                                </span>
                                            </div>
                                            <div class="flex items-center justify-between">
                                                <span class="text-sm text-muted-foreground">Cena za osobę:</span>
                                                <span class="font-bold text-primary flex items-center gap-1">
                                                    {convertPrice(room.perPersonPriceOverride ?? standard()?.pricePerPerson)}
                                                </span>
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>
                            );
                        }}
                    </For>
                </div>
            </Show>
        </div>
    );
}
